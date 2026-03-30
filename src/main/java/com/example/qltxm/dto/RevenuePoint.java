package com.example.qltxm.dto;

public class RevenuePoint {

    private final String label;
    private final double amount;
    private final int percent;

    public RevenuePoint(String label, double amount, int percent) {
        this.label = label;
        this.amount = amount;
        this.percent = percent;
    }

    public String getLabel() {
        return label;
    }

    public double getAmount() {
        return amount;
    }

    public int getPercent() {
        return percent;
    }
}
