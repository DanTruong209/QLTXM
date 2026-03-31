package com.example.qltxm.controller;

import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.repository.MotorbikeRepository;
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
@RequestMapping("/motorbikes")
public class MotorbikeController {

    private final MotorbikeRepository motorbikeRepository;

    public MotorbikeController(MotorbikeRepository motorbikeRepository) {
        this.motorbikeRepository = motorbikeRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) BikeStatus status,
                       Model model) {
        model.addAttribute("motorbikes", motorbikeRepository.search(normalizeKeyword(q), status));
        model.addAttribute("statuses", BikeStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", q == null ? "" : q);
        return "motorbikes/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("motorbike", new Motorbike());
        model.addAttribute("statuses", BikeStatus.values());
        model.addAttribute("pageTitle", "Thêm xe máy");
        return "motorbikes/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("motorbike") Motorbike motorbike,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", BikeStatus.values());
            model.addAttribute("pageTitle", "Thêm xe máy");
            return "motorbikes/form";
        }
        motorbikeRepository.save(motorbike);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm xe mới");
        return "redirect:/motorbikes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Motorbike motorbike = motorbikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe"));
        model.addAttribute("motorbike", motorbike);
        model.addAttribute("statuses", BikeStatus.values());
        model.addAttribute("pageTitle", "Cập nhật xe máy");
        return "motorbikes/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("motorbike") Motorbike motorbike,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", BikeStatus.values());
            model.addAttribute("pageTitle", "Cập nhật xe máy");
            return "motorbikes/form";
        }
        motorbike.setId(id);
        motorbikeRepository.save(motorbike);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật thông tin xe");
        return "redirect:/motorbikes";
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }
}
