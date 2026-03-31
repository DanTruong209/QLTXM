package com.example.qltxm.controller;

import com.example.qltxm.model.Customer;
import com.example.qltxm.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("customers", customerRepository.search(normalizeKeyword(q)));
        model.addAttribute("keyword", q == null ? "" : q);
        return "customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "Thêm khách hàng");
        return "customers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("customer") Customer customer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm khách hàng");
            return "customers/form";
        }
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm khách hàng");
        return "redirect:/customers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng"));
        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Cập nhật khách hàng");
        return "customers/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("customer") Customer customer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Cập nhật khách hàng");
            return "customers/form";
        }
        customer.setId(id);
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật khách hàng");
        return "redirect:/customers";
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
