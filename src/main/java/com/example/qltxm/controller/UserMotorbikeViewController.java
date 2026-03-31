package com.example.qltxm.controller;

import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.repository.MotorbikeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/motorbikes")
public class UserMotorbikeViewController {

    private final MotorbikeRepository motorbikeRepository;

    public UserMotorbikeViewController(MotorbikeRepository motorbikeRepository) {
        this.motorbikeRepository = motorbikeRepository;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Motorbike motorbike = motorbikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe máy"));
        if (motorbike.getStatus() == BikeStatus.MAINTENANCE) {
            throw new IllegalStateException("Xe này đang bảo trì, vui lòng chọn xe khác");
        }
        model.addAttribute("motorbike", motorbike);
        return "user-bike-detail";
    }
}
