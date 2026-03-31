package com.example.qltxm.controller;

import com.example.qltxm.dto.ReturnRentalForm;
import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import com.example.qltxm.repository.CustomerRepository;
import com.example.qltxm.repository.MotorbikeRepository;
import com.example.qltxm.service.RentalService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final MotorbikeRepository motorbikeRepository;
    private final CustomerRepository customerRepository;

    public RentalController(RentalService rentalService,
                            MotorbikeRepository motorbikeRepository,
                            CustomerRepository customerRepository) {
        this.rentalService = rentalService;
        this.motorbikeRepository = motorbikeRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) RentalStatus status,
                       @RequestParam(required = false) String q,
                       Model model) {
        model.addAttribute("rentals", rentalService.search(q, status));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", RentalStatus.values());
        model.addAttribute("keyword", q == null ? "" : q);
        return "rentals/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Rental rental = new Rental();
        rental.setCustomer(new com.example.qltxm.model.Customer());
        rental.setMotorbike(new com.example.qltxm.model.Motorbike());
        model.addAttribute("rental", rental);
        populateForm(model);
        model.addAttribute("pageTitle", "Lập phiếu thuê");
        return "rentals/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("rental") Rental rental,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateForm(model);
            model.addAttribute("pageTitle", "Lập phiếu thuê");
            return "rentals/form";
        }
        try {
            rentalService.create(rental);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo phiếu thuê ở trạng thái chờ duyệt");
            return "redirect:/rentals";
        } catch (RuntimeException ex) {
            model.addAttribute("formError", ex.getMessage());
            populateForm(model);
            model.addAttribute("pageTitle", "Lập phiếu thuê");
            return "rentals/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("rental", rentalService.findById(id));
        ReturnRentalForm returnForm = new ReturnRentalForm();
        returnForm.setActualReturnDate(LocalDate.now());
        model.addAttribute("returnForm", returnForm);
        return "rentals/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("rental", rentalService.findById(id));
        populateForm(model);
        model.addAttribute("pageTitle", "Cập nhật phiếu thuê");
        return "rentals/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("rental") Rental rental,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateForm(model);
            model.addAttribute("pageTitle", "Cập nhật phiếu thuê");
            return "rentals/form";
        }
        try {
            rentalService.update(id, rental);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật phiếu thuê");
            return "redirect:/rentals";
        } catch (RuntimeException ex) {
            model.addAttribute("formError", ex.getMessage());
            populateForm(model);
            model.addAttribute("pageTitle", "Cập nhật phiếu thuê");
            return "rentals/form";
        }
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.approve(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt đơn thuê");
        return "redirect:/rentals/" + id;
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id,
                           @Valid @ModelAttribute("returnForm") ReturnRentalForm returnForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rental", rentalService.findById(id));
            return "rentals/detail";
        }
        rentalService.complete(id, returnForm.getActualReturnDate(), returnForm.getExtraFee(), returnForm.getReturnNotes());
        redirectAttributes.addFlashAttribute("successMessage", "Đã trả xe và hoàn tất phiếu thuê");
        return "redirect:/rentals/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        rentalService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy phiếu thuê");
        return "redirect:/rentals/" + id;
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(defaultValue = "Đơn thuê không phù hợp điều kiện tiếp nhận") String reason,
                         RedirectAttributes redirectAttributes) {
        rentalService.reject(id, reason);
        redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn thuê");
        return "redirect:/rentals/" + id;
    }

    private void populateForm(Model model) {
        model.addAttribute("motorbikes", motorbikeRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
    }
}
