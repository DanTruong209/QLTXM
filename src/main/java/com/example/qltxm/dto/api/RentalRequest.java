package com.example.qltxm.dto.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long motorbikeId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @DecimalMin("0.0")
    private BigDecimal depositAmount;

    private String notes;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

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
