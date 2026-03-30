package com.example.qltxm.dto;

public class StatusSummary {

    private final String label;
    private final long count;

    public StatusSummary(String label, long count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public long getCount() {
        return count;
    }
}
