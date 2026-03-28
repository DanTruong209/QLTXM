package com.example.qltxm.repository;

import com.example.qltxm.model.BikeStatus;
import com.example.qltxm.model.Motorbike;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MotorbikeRepository extends JpaRepository<Motorbike, Long> {

    long countByStatus(BikeStatus status);

    List<Motorbike> findByStatusOrderByBrandAscModelAsc(BikeStatus status);

    @Query("""
            select m from Motorbike m
            where (:status is null or m.status = :status)
              and (
                :keyword is null
                or lower(m.code) like lower(concat('%', :keyword, '%'))
                or lower(m.brand) like lower(concat('%', :keyword, '%'))
                or lower(m.model) like lower(concat('%', :keyword, '%'))
                or lower(m.licensePlate) like lower(concat('%', :keyword, '%'))
              )
            order by m.brand asc, m.model asc
            """)
    List<Motorbike> search(@Param("keyword") String keyword, @Param("status") BikeStatus status);
}
