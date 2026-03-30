package com.example.qltxm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnRentalForm {

    @NotNull(message = "Vui lòng chọn ngày trả thực tế")
    private LocalDate actualReturnDate;

    @DecimalMin(value = "0.0", message = "Phụ phí không được âm")
    private BigDecimal extraFee = BigDecimal.ZERO;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String returnNotes;

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public BigDecimal getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(BigDecimal extraFee) {
        this.extraFee = extraFee;
    }

    public String getReturnNotes() {
        return returnNotes;
    }

    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }
}
