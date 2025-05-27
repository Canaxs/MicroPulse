package com.micro.product_service.service;

import com.dto_common.OrderItemDTO;
import com.micro.product_service.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderDTO createOrder(List<OrderItemDTO> orderItemsDTO, String userId);

    OrderDTO getOrderById(Long orderId);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    OrderDTO updateOrderStatus(Long orderId, String status);

    void deleteOrder(Long orderId);

    OrderDTO addItemsToOrder(Long orderId, List<OrderItemDTO> additionalItemsDTO);

    double calculateTotalAmount(Long orderId);

    Page<OrderDTO> getOrdersByUser(Pageable pageable);

    List<OrderItemDTO> getOrderItems(Long orderId);

    OrderItemDTO updateOrderItem(Long orderItemId, int quantity, double price);

    void removeOrderItem(Long orderItemId);

    Page<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
