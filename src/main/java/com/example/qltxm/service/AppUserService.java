package com.example.qltxm.service;

import com.example.qltxm.dto.RegistrationForm;
import com.example.qltxm.dto.UserProfileForm;
import com.example.qltxm.model.AppUser;
import com.example.qltxm.model.Customer;
import com.example.qltxm.model.UserRole;
import com.example.qltxm.repository.AppUserRepository;
import com.example.qltxm.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository,
                          CustomerRepository customerRepository,
                          PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(RegistrationForm form) {
        if (appUserRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (customerRepository.existsByPhone(form.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }
        if (customerRepository.existsByIdCard(form.getIdCard())) {
            throw new IllegalArgumentException("CCCD/CMND đã được sử dụng");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        Customer customer = new Customer();
        customer.setFullName(form.getFullName().trim());
        customer.setPhone(form.getPhone().trim());
        customer.setIdCard(form.getIdCard().trim());
        customer.setAddress(form.getAddress().trim());
        customer = customerRepository.save(customer);

        AppUser user = new AppUser();
        user.setFullName(form.getFullName().trim());
        user.setUsername(form.getUsername().trim());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(UserRole.USER);
        user.setCustomer(customer);
        return appUserRepository.save(user);
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
    }

    public UserProfileForm getProfileForm(String username) {
        AppUser user = findByUsername(username);
        UserProfileForm form = new UserProfileForm();
        form.setFullName(user.getFullName());
        if (user.getCustomer() != null) {
            form.setPhone(user.getCustomer().getPhone());
            form.setIdCard(user.getCustomer().getIdCard());
            form.setAddress(user.getCustomer().getAddress());
            form.setNotes(user.getCustomer().getNotes());
        }
        return form;
    }

    public void updateProfile(String username, UserProfileForm form) {
        AppUser user = findByUsername(username);
        Customer customer = user.getCustomer();
        if (customer == null) {
            customer = new Customer();
        } else {
            if (customerRepository.existsByPhoneAndIdNot(form.getPhone().trim(), customer.getId())) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
            }
            if (customerRepository.existsByIdCardAndIdNot(form.getIdCard().trim(), customer.getId())) {
                throw new IllegalArgumentException("CCCD/CMND đã được sử dụng");
            }
        }

        user.setFullName(form.getFullName().trim());
        customer.setFullName(form.getFullName().trim());
        customer.setPhone(form.getPhone().trim());
        customer.setIdCard(form.getIdCard().trim());
        customer.setAddress(form.getAddress().trim());
        customer.setNotes(form.getNotes());
        customer = customerRepository.save(customer);
        user.setCustomer(customer);
        appUserRepository.save(user);
    }
}
