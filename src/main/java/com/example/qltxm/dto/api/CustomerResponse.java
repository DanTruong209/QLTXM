package com.example.qltxm.dto.api;

import com.example.qltxm.model.Customer;

public record CustomerResponse(
        Long id,
        String fullName,
        String phone,
        String idCard,
        String address,
        String notes
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getPhone(),
                customer.getIdCard(),
                customer.getAddress(),
                customer.getNotes()
        );
    }
}
