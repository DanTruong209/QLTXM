package com.example.qltxm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UserRentalForm {

    @NotNull(message = "Vui lòng chọn xe máy")
    private Long motorbikeId;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    @FutureOrPresent(message = "Ngày bắt đầu phải từ hôm nay trở đi")
    private LocalDate startDate;

    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    @FutureOrPresent(message = "Ngày kết thúc phải từ hôm nay trở đi")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Tiền cọc không được âm")
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String notes;

    public Long getMotorbikeId() {
        return motorbikeId;
    }

    public void setMotorbikeId(Long motorbikeId) {
        this.motorbikeId = motorbikeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
