package com.payment_service.dto;

import com.dto_common.OrderItemDTO;
import com.iyzipay.model.Address;
import lombok.Data;

import java.util.List;

@Data
public class MakePaymentRequest {
    private PaymentCardDTO paymentCardDTO;
    private List<OrderItemDTO> orderItemDTOS;
    private Address shippingAddress;
    private Address billingAddress;
}
