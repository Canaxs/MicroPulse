package com.micro.product_service.controller;

import com.dto_common.OrderItemDTO;
import com.micro.product_service.dto.OrderDTO;
import com.micro.product_service.service.OrderService;
import com.micro.tokenclaims.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderItemDTO> orderItemsDTO) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(orderService.createOrder(orderItemsDTO, customUserDetails.getUserId()));
    }

    @GetMapping("/order-by/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-items/{orderId}")
    public ResponseEntity<OrderDTO> addItemsToOrder(@PathVariable Long orderId, @RequestBody List<OrderItemDTO> additionalItemsDTO) {
        return ResponseEntity.ok(orderService.addItemsToOrder(orderId, additionalItemsDTO));
    }

    @GetMapping("/total-amount/{orderId}")
    public ResponseEntity<Double> calculateTotalAmount(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.calculateTotalAmount(orderId));
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<OrderDTO>> getOrdersByUser(Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByUser(pageable));
    }

    @GetMapping("/items/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PutMapping("/update-item/{orderItemId}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@PathVariable Long orderItemId, @RequestParam int quantity, @RequestParam double price) {
        return ResponseEntity.ok(orderService.updateOrderItem(orderItemId, quantity, price));
    }

    @DeleteMapping("/remove-item/{orderItemId}")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Long orderItemId) {
        orderService.removeOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<Page<OrderDTO>> getOrdersByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                               Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate, pageable));
    }
}
