package com.payment_service.service.impl;

import com.dto_common.KafkaOrderItemSendDTO;
import com.dto_common.OrderItemDTO;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.micro.tokenclaims.CustomUserDetails;
import com.payment_service.config.SandboxProperties;
import com.payment_service.dto.MakePaymentRequest;
import com.payment_service.dto.PaymentDTO;
import com.payment_service.enums.PaymentMethod;
import com.payment_service.enums.PaymentStatus;
import com.payment_service.persistence.repository.PaymentRepository;
import com.payment_service.service.LogProducerService;
import com.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SandboxProperties sandboxProperties;
    private final LogProducerService logProducerService;

    @Override
    public PaymentDTO processPayment(MakePaymentRequest makePaymentRequest) {
        Options options = new Options();
        options.setApiKey(sandboxProperties.getApiKey());
        options.setSecretKey(sandboxProperties.getSecretKey());
        options.setBaseUrl(sandboxProperties.getBaseUrl());

        BigDecimal orderItemsTotalAmount = calculateTotalAmount(makePaymentRequest.getOrderItemDTOS());

        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(UUID.randomUUID().toString());
        paymentRequest.setPrice(orderItemsTotalAmount);
        paymentRequest.setPaidPrice(orderItemsTotalAmount);
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId(String.valueOf(makePaymentRequest.getOrderItemDTOS().get(0).getOrderId()));
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());


        paymentRequest.setPaymentCard(getPaymentCard(makePaymentRequest));

        paymentRequest.setBuyer(getBuyer());

        List<BasketItem> basketItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : makePaymentRequest.getOrderItemDTOS()) {
            for (int i = 0; i < itemDTO.getQuantity(); i++) {
                BasketItem item = new BasketItem();
                item.setId(itemDTO.getProductId() + "-" + i);
                item.setName("Product #" + itemDTO.getProductId());
                item.setCategory1("General");
                item.setItemType(BasketItemType.PHYSICAL.name());
                item.setPrice(BigDecimal.valueOf(itemDTO.getPrice()));

                basketItems.add(item);
            }
        }
        paymentRequest.setBasketItems(basketItems);

        paymentRequest.setShippingAddress(makePaymentRequest.getShippingAddress());
        paymentRequest.setBillingAddress(makePaymentRequest.getBillingAddress());

        Payment payment = Payment.create(paymentRequest, options);

        if ("success".equalsIgnoreCase(payment.getStatus())) {
            com.payment_service.persistence.entity.Payment saved = paymentRepository.save(
                    com.payment_service.persistence.entity.Payment.builder()
                            .orderId(makePaymentRequest.getOrderItemDTOS().get(0).getOrderId())
                            .amount(orderItemsTotalAmount)
                            .paymentMethod(PaymentMethod.CREDIT_CARD)
                            .status(PaymentStatus.COMPLETED)
                            .paymentDate(LocalDateTime.now())
                            .build()
            );
            CustomUserDetails customUserDetails = getUserDetails();
            logProducerService.sendOrder(new KafkaOrderItemSendDTO(makePaymentRequest.getOrderItemDTOS(),customUserDetails.getUserId()));
            return convertToDTO(saved);
        } else {
            paymentRepository.save(
                    com.payment_service.persistence.entity.Payment.builder()
                            .orderId(makePaymentRequest.getOrderItemDTOS().get(0).getOrderId())
                            .amount(orderItemsTotalAmount)
                            .paymentMethod(PaymentMethod.CREDIT_CARD)
                            .status(PaymentStatus.FAILED)
                            .paymentDate(LocalDateTime.now())
                            .build()
            );
            throw new RuntimeException("Payment failed: " + payment.getErrorMessage());
        }
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        return convertToDTO(paymentRepository.findById(id).get());
    }

    @Override
    public void cancelPayment(Long id) {
        com.payment_service.persistence.entity.Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        List<com.payment_service.persistence.entity.Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public static BigDecimal calculateTotalAmount(List<OrderItemDTO> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderItems.stream()
                .map(item -> BigDecimal.valueOf(item.getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CustomUserDetails getUserDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    private static PaymentCard getPaymentCard(MakePaymentRequest makePaymentRequest) {
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(makePaymentRequest.getPaymentCardDTO().getCardHolderName());
        paymentCard.setCardNumber(makePaymentRequest.getPaymentCardDTO().getCardNumber());
        paymentCard.setExpireMonth(makePaymentRequest.getPaymentCardDTO().getExpireMonth());
        paymentCard.setExpireYear(makePaymentRequest.getPaymentCardDTO().getExpireYear());
        paymentCard.setCvc(makePaymentRequest.getPaymentCardDTO().getCvc());
        paymentCard.setRegisterCard(makePaymentRequest.getPaymentCardDTO().getRegisterCard());
        return paymentCard;
    }

    private Buyer getBuyer() {
        CustomUserDetails customUserDetails = getUserDetails();
        Buyer buyer = new Buyer();
        buyer.setId(customUserDetails.getUserId());
        buyer.setName(customUserDetails.getUsername());
        buyer.setSurname("Doe");
        buyer.setEmail("john.doe@example.com");
        buyer.setIdentityNumber("74300864791");
        buyer.setRegistrationAddress("Istanbul");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34000");
        return buyer;
    }

    private PaymentDTO convertToDTO(com.payment_service.persistence.entity.Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .orderId(payment.getOrderId())
                .paymentDate(payment.getPaymentDate())
                .transactionId(payment.getTransactionId())
                .build();
    }
}
