package com.example.qltxm.controller;

import com.example.qltxm.dto.RegistrationForm;
import com.example.qltxm.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            appUserService.registerUser(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("formError", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/" : "redirect:/user";
    }

    @GetMapping("/error/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorTitle", "Không có quyền truy cập");
        model.addAttribute("errorMessage", "Bạn không có quyền xem nội dung này. Hãy đăng nhập bằng đúng vai trò.");
        return "error-page";
    }
}
