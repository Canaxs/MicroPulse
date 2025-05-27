package com.micro.product_service.service.impl;

import com.dto_common.OrderItemDTO;
import com.micro.product_service.annotation.LogIgnore;
import com.micro.product_service.dto.OrderDTO;
import com.micro.product_service.persistence.entity.Order;
import com.micro.product_service.persistence.entity.OrderItem;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.OrderItemRepository;
import com.micro.product_service.persistence.repository.OrderRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.OrderService;
import com.micro.tokenclaims.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;


    @Override
    @CachePut(cacheNames = "orders", key = "#result.id")
    @CacheEvict(cacheNames = {"orders_all", "orders_by_user", "orders_by_date"}, allEntries = true)
    @LogIgnore
    public OrderDTO createOrder(List<OrderItemDTO> orderItemsDTO ,String userId) {

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO dto : orderItemsDTO) {
            Product product = productRepository.findById(dto.getProductId()).get();
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setPrice(product.getPrice());
            item.setOrder(order);
            items.add(item);
        }

        order.setItems(items);
        order.setUserId(Long.valueOf(userId));
        order.setTotalAmount(
                items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum()
        );

        Order savedOrder = orderRepository.save(order);

        return toOrderDTO(savedOrder);
    }

    @Override
    @Cacheable(cacheNames = "orders", key = "#orderId")
    public OrderDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> new OrderDTO(
                    order.getId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    toOrderItems(order.getItems())
                ))
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    @Cacheable(cacheNames = "orders_all")
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order -> new OrderDTO(
            order.getId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            order.getStatus(),
            toOrderItems(order.getItems())
        ));
    }

    @Override
    @CachePut(cacheNames = "orders", key = "#orderId")
    @CacheEvict(cacheNames = {"orders_all", "orders_by_user", "orders_by_date"}, allEntries = true)
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return toOrderDTO(order);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "orders", key = "#orderId"),
            @CacheEvict(cacheNames = {"orders_all", "orders_by_user", "orders_by_date"}, allEntries = true)
    })
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public OrderDTO addItemsToOrder(Long orderId, List<OrderItemDTO> additionalItemsDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        List<OrderItem> items = additionalItemsDTO.stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProduct(productRepository.findById(dto.getProductId()).get());
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setOrder(order);
            return item;
        }).toList();

        order.getItems().addAll(items);
        orderRepository.save(order);

        return toOrderDTO(order);
    }

    @Override
    public double calculateTotalAmount(Long orderId) {
        return orderRepository.calculateTotalAmount(orderId);
    }

    @Override
    @Cacheable(cacheNames = "orders_by_user", key = "#userId")
    public Page<OrderDTO> getOrdersByUser(Pageable pageable) {
        CustomUserDetails customUserDetails = getUserDetails();
        return orderRepository.findByUserId(Long.parseLong(customUserDetails.getUserId()), pageable).map(order -> new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                toOrderItems(order.getItems())
        ));
    }

    @Override
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public OrderItemDTO updateOrderItem(Long orderItemId, int quantity, double price) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found"));

        item.setQuantity(quantity);
        item.setPrice(price);
        orderItemRepository.save(item);

        return toOrderItemDTO(item);
    }

    @Override
    public void removeOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }

    @Override
    @Cacheable(cacheNames = "orders_by_date", key = "T(java.util.Objects).hash(#startDate, #endDate)")
    public Page<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable).map(order -> new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                toOrderItems(order.getItems())
        ));
    }

    public List<OrderItemDTO> toOrderItems(List<OrderItem> orderItems) {
        return orderItems.stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            return itemDTO;
        }).toList();
    }

    private OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        List<OrderItemDTO> items = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    private CustomUserDetails getUserDetails() {
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
