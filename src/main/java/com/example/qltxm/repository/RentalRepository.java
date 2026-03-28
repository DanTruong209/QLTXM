package com.example.qltxm.repository;

import com.example.qltxm.model.Rental;
import com.example.qltxm.model.RentalStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByStatus(RentalStatus status);

    long countByStatusIn(List<RentalStatus> statuses);

    long countByCustomerIdAndStatus(Long customerId, RentalStatus status);

    List<Rental> findTop5ByOrderByStartDateDescIdDesc();

    List<Rental> findAllByOrderByStartDateDescIdDesc();

    List<Rental> findAllByStatusOrderByStartDateDescIdDesc(RentalStatus status);

    List<Rental> findAllByCustomerIdOrderByStartDateDescIdDesc(Long customerId);

    @Query("""
            select r from Rental r
            where (:status is null or r.status = :status)
              and (
                :keyword is null
                or lower(r.customer.fullName) like lower(concat('%', :keyword, '%'))
                or lower(r.motorbike.brand) like lower(concat('%', :keyword, '%'))
                or lower(r.motorbike.model) like lower(concat('%', :keyword, '%'))
                or lower(r.motorbike.licensePlate) like lower(concat('%', :keyword, '%'))
              )
            order by r.startDate desc, r.id desc
            """)
    List<Rental> search(@Param("status") RentalStatus status, @Param("keyword") String keyword);
}
