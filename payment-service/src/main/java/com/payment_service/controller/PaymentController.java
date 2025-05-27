package com.payment_service.controller;

import com.payment_service.dto.MakePaymentRequest;
import com.payment_service.dto.PaymentDTO;
import com.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> makePayment(@RequestBody MakePaymentRequest makePaymentRequest) {
        return ResponseEntity.ok(paymentService.processPayment(makePaymentRequest));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPayments(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrderId(orderId));
    }
}
