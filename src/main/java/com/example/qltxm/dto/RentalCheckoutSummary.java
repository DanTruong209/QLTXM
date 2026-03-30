package com.example.qltxm.dto;

import com.example.qltxm.model.Motorbike;
import java.math.BigDecimal;

public class RentalCheckoutSummary {

    private final Motorbike motorbike;
    private final long rentalDays;
    private final BigDecimal dailyRate;
    private final BigDecimal rentalTotal;
    private final BigDecimal depositAmount;
    private final BigDecimal remainingAmount;

    public RentalCheckoutSummary(Motorbike motorbike,
                                 long rentalDays,
                                 BigDecimal dailyRate,
                                 BigDecimal rentalTotal,
                                 BigDecimal depositAmount,
                                 BigDecimal remainingAmount) {
        this.motorbike = motorbike;
        this.rentalDays = rentalDays;
        this.dailyRate = dailyRate;
        this.rentalTotal = rentalTotal;
        this.depositAmount = depositAmount;
        this.remainingAmount = remainingAmount;
    }

    public Motorbike getMotorbike() {
        return motorbike;
    }

    public long getRentalDays() {
        return rentalDays;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public BigDecimal getRentalTotal() {
        return rentalTotal;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
}
