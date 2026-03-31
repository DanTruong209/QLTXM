package com.example.qltxm.controller;

import com.example.qltxm.dto.RentalCheckoutSummary;
import com.example.qltxm.dto.UserPaymentForm;
import com.example.qltxm.dto.UserProfileForm;
import com.example.qltxm.dto.UserRentalForm;
import com.example.qltxm.model.AppUser;
import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.PaymentMethod;
import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import com.example.qltxm.repository.MotorbikeRepository;
import com.example.qltxm.service.AppUserService;
import com.example.qltxm.service.RentalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    private final MotorbikeRepository motorbikeRepository;
    private final RentalService rentalService;
    private final AppUserService appUserService;

    public UserController(MotorbikeRepository motorbikeRepository,
                          RentalService rentalService,
                          AppUserService appUserService) {
        this.motorbikeRepository = motorbikeRepository;
        this.rentalService = rentalService;
        this.appUserService = appUserService;
    }

    @GetMapping
    public String home(Authentication authentication,
                       @RequestParam(required = false) String q,
                       Model model) {
        AppUser user = appUserService.findByUsername(authentication.getName());
        Long customerId = user.getCustomer().getId();
        List<Rental> rentals = rentalService.findAllByCustomerId(customerId);
        model.addAttribute("appUser", user);
        model.addAttribute("availableBikes", motorbikeRepository.search(normalizeKeyword(q), BikeStatus.AVAILABLE));
        model.addAttribute("rentals", rentals);
        model.addAttribute("activeRentalCount", rentalService.countActiveByCustomerId(customerId));
        model.addAttribute("pendingRentalCount", rentals.stream().filter(r -> r.getStatus() == RentalStatus.PENDING).count());
        model.addAttribute("completedRentalCount", rentals.stream().filter(r -> r.getStatus() == RentalStatus.COMPLETED).count());
        model.addAttribute("keyword", q == null ? "" : q);
        return "user-home";
    }

    @GetMapping("/rentals/new")
    public String bookingForm(Authentication authentication, Model model) {
        AppUser user = appUserService.findByUsername(authentication.getName());
        model.addAttribute("appUser", user);
        model.addAttribute("userRentalForm", new UserRentalForm());
        model.addAttribute("availableBikes", motorbikeRepository.findByStatusOrderByBrandAscModelAsc(BikeStatus.AVAILABLE));
        return "user-rental-form";
    }

    @PostMapping("/rentals/checkout")
    public String checkout(Authentication authentication,
                           @Valid @ModelAttribute("userRentalForm") UserRentalForm form,
                           BindingResult bindingResult,
                           Model model) {
        AppUser user = appUserService.findByUsername(authentication.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("appUser", user);
            model.addAttribute("availableBikes", motorbikeRepository.findByStatusOrderByBrandAscModelAsc(BikeStatus.AVAILABLE));
            return "user-rental-form";
        }
        try {
            RentalCheckoutSummary summary = rentalService.prepareCheckout(form);
            UserPaymentForm paymentForm = new UserPaymentForm();
            paymentForm.setMotorbikeId(form.getMotorbikeId());
            paymentForm.setStartDate(form.getStartDate());
            paymentForm.setEndDate(form.getEndDate());
            paymentForm.setDepositAmount(form.getDepositAmount());
            paymentForm.setNotes(form.getNotes());
            return renderCheckoutPage(user, paymentForm, summary, model);
        } catch (RuntimeException ex) {
            model.addAttribute("formError", ex.getMessage());
            model.addAttribute("appUser", user);
            model.addAttribute("availableBikes", motorbikeRepository.findByStatusOrderByBrandAscModelAsc(BikeStatus.AVAILABLE));
            return "user-rental-form";
        }
    }

    @PostMapping("/rentals")
    public String createBooking(Authentication authentication,
                                @Valid @ModelAttribute("paymentForm") UserPaymentForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        AppUser user = appUserService.findByUsername(authentication.getName());
        if (bindingResult.hasErrors()) {
            return renderCheckoutPage(user, form, model);
        }
        try {
            rentalService.createForCustomer(user.getCustomer().getId(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Da ghi nhan thanh toan coc va gui yeu cau thue xe.");
            return "redirect:/user";
        } catch (RuntimeException ex) {
            model.addAttribute("formError", ex.getMessage());
            return renderCheckoutPage(user, form, model);
        }
    }

    @PostMapping("/rentals/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        AppUser user = appUserService.findByUsername(authentication.getName());
        Rental rental = rentalService.findById(id);
        if (!rental.getCustomer().getId().equals(user.getCustomer().getId())) {
            throw new IllegalStateException("Ban khong duoc phep thao tac voi phieu thue nay");
        }
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new IllegalStateException("Chi co the huy yeu cau dang cho duyet");
        }
        rentalService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Da huy yeu cau thue");
        return "redirect:/user";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("profileForm", appUserService.getProfileForm(authentication.getName()));
        return "user-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication authentication,
                                @Valid @ModelAttribute("profileForm") UserProfileForm form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "user-profile";
        }
        try {
            appUserService.updateProfile(authentication.getName(), form);
            redirectAttributes.addFlashAttribute("successMessage", "Da cap nhat ho so");
            return "redirect:/user/profile";
        } catch (RuntimeException ex) {
            model.addAttribute("formError", ex.getMessage());
            return "user-profile";
        }
    }

    private String renderCheckoutPage(AppUser user, UserPaymentForm form, Model model) {
        return renderCheckoutPage(user, form, rentalService.prepareCheckout(toUserRentalForm(form)), model);
    }

    private String renderCheckoutPage(AppUser user,
                                      UserPaymentForm form,
                                      RentalCheckoutSummary summary,
                                      Model model) {
        model.addAttribute("appUser", user);
        model.addAttribute("checkoutSummary", summary);
        model.addAttribute("paymentForm", form);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "user-payment";
    }

    private UserRentalForm toUserRentalForm(UserPaymentForm form) {
        UserRentalForm rentalForm = new UserRentalForm();
        rentalForm.setMotorbikeId(form.getMotorbikeId());
        rentalForm.setStartDate(form.getStartDate());
        rentalForm.setEndDate(form.getEndDate());
        rentalForm.setDepositAmount(form.getDepositAmount());
        rentalForm.setNotes(form.getNotes());
        return rentalForm;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
