package com.micro.product_service.unit;

import com.dto_common.OrderItemDTO;
import com.micro.product_service.dto.OrderDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.entity.Order;
import com.micro.product_service.persistence.entity.OrderItem;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.OrderItemRepository;
import com.micro.product_service.persistence.repository.OrderRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.impl.OrderServiceImpl;
import com.micro.tokenclaims.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository,productRepository,orderItemRepository);
    }

    void setUpSecurityAuthenticate() {
        CustomUserDetails userDetails = new CustomUserDetails("1","meric","user");

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void create_shouldSaveOrder() {
        setUpSecurityAuthenticate();
        Category category1 = new Category(1L,"Erkek",null,null);
        Category category2 = new Category(2L,"Kadın",null,null);

        Product product1 = new Product(1L,"Parfüm",200.0,40,category1);
        Product product2 = new Product(2L,"Ayakkabı",450.0,30,category2);

        Order order = new Order(1L, LocalDateTime.now(),0,"PENDING",1L,null);

        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, 2, product1.getPrice(), product1,order),
                new OrderItem(2L, 3, product2.getPrice(), product2,order)
        );

        List<OrderItemDTO> orderItemDTOS = List.of(
                new OrderItemDTO(1L, 2, product1.getPrice(), product1.getId(),order.getId()),
                new OrderItemDTO(2L, 3, product2.getPrice(), product2.getId(),order.getId())
        );

        Order saveOrder = new Order(1L, LocalDateTime.now(),200.0 * 2 + 450.0 * 3,"PENDING",1L,orderItems);

        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        when(productRepository.findById(product2.getId())).thenReturn(Optional.of(product2));

        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);

        OrderDTO result = orderService.createOrder(orderItemDTOS,"1");

        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(200.0 * 2 + 450.0 * 3, result.getTotalAmount());
        assertEquals("PENDING", result.getStatus());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order captured = captor.getValue();

        assertEquals(orderItems.size(), captured.getItems().size());
        assertEquals("PENDING", captured.getStatus());

        verify(productRepository).findById(product1.getId());
        verify(productRepository).findById(product2.getId());
        verify(orderRepository).save(any(Order.class));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderById_shouldReturnOrderDTO_whenOrderExists() {
        Order order = new Order(1L, LocalDateTime.now(), 500.0, "PENDING", 1L, List.of());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getStatus());

        verify(orderRepository).findById(1L);
    }

    @Test
    void updateOrderStatus_shouldUpdateAndReturnOrderDTO() {
        Order order = new Order(1L, LocalDateTime.now(), 500.0, "PENDING", 1L, List.of());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.updateOrderStatus(1L, "SHIPPED");

        assertNotNull(result);
        assertEquals("SHIPPED", result.getStatus());

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void deleteOrder_shouldCallRepositoryDeleteById() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void addItemsToOrder_shouldAddItemsAndReturnUpdatedOrderDTO() {
        Product product = new Product(1L, "Ürün", 100.0, 10, new Category());

        Order order = new Order(1L, LocalDateTime.now(), 200.0, "PENDING", 1L, new ArrayList<>());

        OrderItemDTO itemDTO = new OrderItemDTO(null, 2, 100.0, 1L, 1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO result = orderService.addItemsToOrder(1L, List.of(itemDTO));

        assertNotNull(result);
        assertEquals(1, result.getItems().size());

        verify(orderRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void calculateTotalAmount_shouldReturnValueFromRepository() {
        when(orderRepository.calculateTotalAmount(1L)).thenReturn(123.45);

        double result = orderService.calculateTotalAmount(1L);

        assertEquals(123.45, result);

        verify(orderRepository).calculateTotalAmount(1L);
    }

    @Test
    void getOrderItems_shouldReturnListOfOrderItemDTO() {
        OrderItemDTO dto1 = new OrderItemDTO(1L, 1, 100.0, 1L, 1L);
        OrderItemDTO dto2 = new OrderItemDTO(2L, 2, 200.0, 2L, 1L);

        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(dto1, dto2));

        List<OrderItemDTO> result = orderService.getOrderItems(1L);

        assertEquals(2, result.size());
        verify(orderItemRepository).findByOrderId(1L);
    }

    @Test
    void updateOrderItem_shouldUpdateAndReturnDTO() {
        Product product = new Product(1L, "Ürün", 100.0, 10, new Category());
        OrderItem item = new OrderItem(1L, 1, 100.0, product, new Order());

        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(item);

        OrderItemDTO result = orderService.updateOrderItem(1L, 3, 150.0);

        assertEquals(3, result.getQuantity());
        assertEquals(150.0, result.getPrice());

        verify(orderItemRepository).findById(1L);
        verify(orderItemRepository).save(item);
    }

    @Test
    void removeOrderItem_shouldCallDeleteById() {
        doNothing().when(orderItemRepository).deleteById(1L);

        orderService.removeOrderItem(1L);

        verify(orderItemRepository).deleteById(1L);
    }

    @Test
    void getOrdersByDateRange_shouldReturnPagedResult() {
        Order order = new Order(1L, LocalDateTime.now(), 100.0, "PENDING", 1L, List.of());

        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findByCreatedAtBetween(any(), any(), any())).thenReturn(page);

        Page<OrderDTO> result = orderService.getOrdersByDateRange(LocalDateTime.now().minusDays(1), LocalDateTime.now(), Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        verify(orderRepository).findByCreatedAtBetween(any(), any(), any());
    }
}
