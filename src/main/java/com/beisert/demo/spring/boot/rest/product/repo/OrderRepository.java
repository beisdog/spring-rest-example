package com.beisert.demo.spring.boot.rest.product.repo;

import com.beisert.demo.spring.boot.rest.product.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.orderedAt > :from AND o.orderedAt <= :to")
    Page<Order> fromTo(
            @Param("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @Param("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            Pageable pageable
    );
}
