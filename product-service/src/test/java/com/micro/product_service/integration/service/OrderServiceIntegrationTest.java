package com.micro.product_service.integration.service;

import com.micro.product_service.dto.OrderDTO;
import com.micro.product_service.dto.OrderItemDTO;
import com.micro.product_service.persistence.entity.Category;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.repository.OrderItemRepository;
import com.micro.product_service.persistence.repository.OrderRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import com.micro.product_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {


    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void createOrder_shouldCreateAndReturnOrderDTO() {
        Category category = new Category();
        category.setName("TestKategori");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Ürün");
        product.setPrice(100.0);
        product.setStock(10);
        product.setCategory(category);
        product = productRepository.save(product);

        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        OrderDTO createdOrder = orderService.createOrder(List.of(item));

        assertNotNull(createdOrder.getId());
        assertEquals("PENDING", createdOrder.getStatus());
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(200.0, createdOrder.getTotalAmount());
    }

    @Test
    void getOrderById_shouldReturnCorrectOrder() {
        OrderDTO created = createDummyOrder();
        OrderDTO fetched = orderService.getOrderById(created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals(created.getTotalAmount(), fetched.getTotalAmount());
    }

    @Test
    void getAllOrders_shouldReturnPage() {
        createDummyOrder();
        Page<OrderDTO> page = orderService.getAllOrders(PageRequest.of(0, 10));

        assertFalse(page.isEmpty());
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        OrderDTO order = createDummyOrder();
        OrderDTO updated = orderService.updateOrderStatus(order.getId(), "SHIPPED");

        assertEquals("SHIPPED", updated.getStatus());
    }

    @Test
    void deleteOrder_shouldRemoveOrder() {
        OrderDTO order = createDummyOrder();
        orderService.deleteOrder(order.getId());

        assertThrows(Exception.class, () -> orderService.getOrderById(order.getId()));
    }

    @Test
    void addItemsToOrder_shouldAppendItems() {
        OrderDTO order = createDummyOrder();

        Product product2 = new Product();
        product2.setName("Ek Ürün");
        product2.setPrice(50.0);
        product2.setStock(10);
        product2.setCategory(categoryRepository.findAll().get(0));
        product2 = productRepository.save(product2);

        OrderItemDTO extra = new OrderItemDTO();
        extra.setProductId(product2.getId());
        extra.setQuantity(3);
        extra.setPrice(50.0);

        OrderDTO updated = orderService.addItemsToOrder(order.getId(), List.of(extra));

        assertEquals(2, updated.getItems().size());
    }

    @Test
    void calculateTotalAmount_shouldReturnCorrectAmount() {
        OrderDTO order = createDummyOrder();
        double total = orderService.calculateTotalAmount(order.getId());

        assertEquals(order.getTotalAmount(), total);
    }

    @Test
    void updateOrderItem_shouldChangeQuantityAndPrice() {
        OrderDTO order = createDummyOrder();
        OrderItemDTO item = order.getItems().get(0);

        OrderItemDTO updated = orderService.updateOrderItem(item.getId(), 5, 120.0);

        assertEquals(5, updated.getQuantity());
        assertEquals(120.0, updated.getPrice());
    }

    @Test
    void removeOrderItem_shouldDeleteItem() {
        OrderDTO order = createDummyOrder();
        OrderItemDTO item = order.getItems().get(0);

        orderService.removeOrderItem(item.getId());

        List<OrderItemDTO> remaining = orderService.getOrderItems(order.getId());
        assertTrue(remaining.isEmpty());
    }

    @Test
    void getOrdersByDateRange_shouldReturnCorrectPage() {
        createDummyOrder();
        LocalDateTime now = LocalDateTime.now();
        Page<OrderDTO> orders = orderService.getOrdersByDateRange(now.minusDays(1), now.plusDays(1), PageRequest.of(0, 10));

        assertFalse(orders.isEmpty());
    }

    private OrderDTO createDummyOrder() {
        Category category = new Category();
        category.setName("Kategori");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Ürün");
        product.setPrice(100.0);
        product.setStock(50);
        product.setCategory(category);
        product = productRepository.save(product);

        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(product.getId());
        item.setQuantity(1);

        return orderService.createOrder(List.of(item));
    }
}
