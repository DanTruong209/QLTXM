package com.example.qltxm.repository;

import com.example.qltxm.model.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByIdCardAndIdNot(String idCard, Long id);

    boolean existsByPhone(String phone);

    boolean existsByIdCard(String idCard);

    @Query("""
            select c from Customer c
            where :keyword is null
               or lower(c.fullName) like lower(concat('%', :keyword, '%'))
               or lower(c.phone) like lower(concat('%', :keyword, '%'))
               or lower(c.idCard) like lower(concat('%', :keyword, '%'))
               or lower(coalesce(c.address, '')) like lower(concat('%', :keyword, '%'))
            order by c.fullName asc
            """)
    List<Customer> search(@Param("keyword") String keyword);
}
