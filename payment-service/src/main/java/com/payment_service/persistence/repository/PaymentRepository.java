package com.payment_service.persistence.repository;

import com.payment_service.persistence.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment , Long> {

    List<Payment> findByOrderId(Long orderId);
}
