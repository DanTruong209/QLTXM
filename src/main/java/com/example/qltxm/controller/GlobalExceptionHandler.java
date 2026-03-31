package com.example.qltxm.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Không tìm thấy dữ liệu");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-page";
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public String handleBusinessError(RuntimeException ex, Model model) {
        model.addAttribute("errorTitle", "Không thể thực hiện thao tác");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-page";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataConflict(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("errorTitle", "Dữ liệu bị trùng hoặc không hợp lệ");
        model.addAttribute("errorMessage", "Vui lòng kiểm tra lại dữ liệu nhập vào, có thể số điện thoại, CCCD hoặc mã xe đã tồn tại.");
        return "error-page";
    }
}
