package com.example.qltxm.config;

import com.example.qltxm.model.AppUser;
import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Customer;
import com.example.qltxm.model.Motorbike;
import com.example.qltxm.model.UserRole;
import com.example.qltxm.repository.AppUserRepository;
import com.example.qltxm.repository.CustomerRepository;
import com.example.qltxm.repository.MotorbikeRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(MotorbikeRepository motorbikeRepository,
                               CustomerRepository customerRepository,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (motorbikeRepository.count() == 0) {
                motorbikeRepository.save(createBike(
                        "XM001",
                        "Honda",
                        "Vision",
                        "59A1-12345",
                        "Xe ga tiết kiệm xăng",
                        "/images/bikes/honda-vision.svg"
                ));
                motorbikeRepository.save(createBike(
                        "XM002",
                        "Yamaha",
                        "Exciter 155",
                        "59A1-54321",
                        "Xe côn tay cho khách du lịch",
                        "/images/bikes/yamaha-exciter.svg"
                ));
                motorbikeRepository.save(createBike(
                        "XM003",
                        "Honda",
                        "Winner X",
                        "59A1-67890",
                        "Xe số mạnh cho thuê ngày",
                        "/images/bikes/honda-winner.svg"
                ));
            }

            if (customerRepository.count() == 0) {
                customerRepository.save(createCustomer("Nguyễn Văn An", "0901234567", "079123456789"));
                customerRepository.save(createCustomer("Trần Thị Bình", "0912345678", "079987654321"));
            }

            if (!appUserRepository.existsByUsername("admin")) {
                appUserRepository.save(createUser("Quản trị viên", "admin", "admin123", UserRole.ADMIN, passwordEncoder));
            }

            if (!appUserRepository.existsByUsername("user")) {
                Customer customer = customerRepository.findAll().stream().findFirst()
                        .orElseGet(() -> customerRepository.save(createCustomer("Khách demo", "0988888888", "079000000001")));
                appUserRepository.save(createUser("Khách demo", "user", "user123", UserRole.USER, passwordEncoder, customer));
            } else {
                appUserRepository.findByUsername("user").ifPresent(user -> {
                    if (user.getCustomer() == null) {
                        Customer customer = customerRepository.findAll().stream().findFirst()
                                .orElseGet(() -> customerRepository.save(createCustomer("Khách demo", "0988888888", "079000000001")));
                        user.setCustomer(customer);
                        appUserRepository.save(user);
                    }
                });
            }
        };
    }

    private Motorbike createBike(String code, String brand, String model, String plate, String notes, String imageUrl) {
        Motorbike bike = new Motorbike();
        bike.setCode(code);
        bike.setBrand(brand);
        bike.setModel(model);
        bike.setLicensePlate(plate);
        bike.setDailyRate(new BigDecimal("180000"));
        bike.setStatus(BikeStatus.AVAILABLE);
        bike.setNotes(notes);
        bike.setImageUrl(imageUrl);
        return bike;
    }

    private Customer createCustomer(String name, String phone, String idCard) {
        Customer customer = new Customer();
        customer.setFullName(name);
        customer.setPhone(phone);
        customer.setIdCard(idCard);
        customer.setAddress("Hồ Chí Minh");
        return customer;
    }

    private AppUser createUser(String fullName,
                               String username,
                               String rawPassword,
                               UserRole role,
                               PasswordEncoder passwordEncoder) {
        return createUser(fullName, username, rawPassword, role, passwordEncoder, null);
    }

    private AppUser createUser(String fullName,
                               String username,
                               String rawPassword,
                               UserRole role,
                               PasswordEncoder passwordEncoder,
                               Customer customer) {
        AppUser user = new AppUser();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setCustomer(customer);
        return user;
    }
}
