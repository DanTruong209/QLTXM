package com.example.qltxm.util;

import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.PaymentMethod;
import com.example.qltxm.model.PaymentStatus;
import com.example.qltxm.model.RentalStatus;
import org.springframework.stereotype.Component;

@Component("viewUtils")
public class ViewUtils {

    public String bikeStatusLabel(BikeStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case AVAILABLE -> "San sang";
            case RENTED -> "Dang cho thue";
            case MAINTENANCE -> "Bao tri";
        };
    }

    public String rentalStatusLabel(RentalStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case PENDING -> "Cho duyet";
            case ACTIVE, APPROVED -> "Dang hoat dong";
            case COMPLETED -> "Da hoan tat";
            case CANCELLED -> "Da huy";
            case REJECTED -> "Da tu choi";
        };
    }

    public String paymentMethodLabel(PaymentMethod method) {
        if (method == null) {
            return "";
        }
        return switch (method) {
            case CASH -> "Tien mat";
            case BANK_TRANSFER -> "Chuyen khoan";
            case MOMO -> "MoMo";
        };
    }

    public String paymentStatusLabel(PaymentStatus status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case UNPAID -> "Chua thanh toan";
            case DEPOSIT_PAID -> "Da coc";
            case PAID_IN_FULL -> "Da thanh toan du";
            case REFUNDED -> "Da hoan tien";
        };
    }
}
