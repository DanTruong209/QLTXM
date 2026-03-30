package com.example.qltxm.dto;

import com.example.qltxm.model.PaymentMethod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UserPaymentForm {

    @NotNull(message = "Vui long chon xe may")
    private Long motorbikeId;

    @NotNull(message = "Vui long chon ngay bat dau")
    @FutureOrPresent(message = "Ngay bat dau phai tu hom nay tro di")
    private LocalDate startDate;

    @NotNull(message = "Vui long chon ngay ket thuc")
    @FutureOrPresent(message = "Ngay ket thuc phai tu hom nay tro di")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Tien coc khong duoc am")
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Size(max = 500, message = "Ghi chu toi da 500 ky tu")
    private String notes;

    @NotNull(message = "Vui long chon phuong thuc thanh toan")
    private PaymentMethod paymentMethod;

    @AssertTrue(message = "Ban can xac nhan da thanh toan tien coc")
    private boolean depositConfirmed;

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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isDepositConfirmed() {
        return depositConfirmed;
    }

    public void setDepositConfirmed(boolean depositConfirmed) {
        this.depositConfirmed = depositConfirmed;
    }
}
