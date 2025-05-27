package com.payment_service.service;

import com.payment_service.dto.MakePaymentRequest;
import com.payment_service.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO processPayment(MakePaymentRequest makePaymentRequest);
    PaymentDTO getPaymentById(Long id);
    void cancelPayment(Long id);

    List<PaymentDTO> getPaymentsByOrderId(Long orderId);
}
