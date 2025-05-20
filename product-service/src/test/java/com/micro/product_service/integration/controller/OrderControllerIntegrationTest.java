package com.micro.product_service.integration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.product_service.dto.OrderItemDTO;
import com.micro.product_service.persistence.entity.OrderItem;
import com.micro.product_service.persistence.entity.Product;
import com.micro.product_service.persistence.repository.CategoryRepository;
import com.micro.product_service.persistence.repository.OrderItemRepository;
import com.micro.product_service.persistence.repository.OrderRepository;
import com.micro.product_service.persistence.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(50);
        productRepository.save(product);
        productId = product.getId();
    }

    private String createOrderAndReturnId() throws Exception {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setPrice(100.0);

        String json = objectMapper.writeValueAsString(List.of(item));

        String response = mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asText();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setPrice(100.0);

        String json = objectMapper.writeValueAsString(List.of(item));

        mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItems[0].productId").value(productId))
                .andExpect(jsonPath("$.totalAmount").value(200.0));
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(1);
        item.setPrice(100.0);
        String json = objectMapper.writeValueAsString(List.of(item));

        String response = mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/order/order-by/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    void shouldReturnAllOrder() throws Exception {
        createOrderAndReturnId();

        mockMvc.perform(get("/order/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldUpdateOrderStatus() throws Exception {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(1);
        item.setPrice(100.0);
        String json = objectMapper.writeValueAsString(List.of(item));

        String response = mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/order/status/" + orderId)
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(1);
        item.setPrice(100.0);
        String json = objectMapper.writeValueAsString(List.of(item));

        String response = mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/order/delete/" + orderId))
                .andExpect(status().isOk());
    }

    @Test
    void addItemsToOrder() throws Exception {
        String orderId = createOrderAndReturnId();

        OrderItemDTO newItem = new OrderItemDTO();
        newItem.setProductId(productId);
        newItem.setQuantity(3);
        newItem.setPrice(100.0);

        mockMvc.perform(post("/order/add-items/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(newItem))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderItems.length()").value(2));
    }

    @Test
    void calculateTotalAmount() throws Exception {
        String orderId = createOrderAndReturnId();

        mockMvc.perform(get("/order/total-amount/" + orderId))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void getOrdersByUser() throws Exception {
        createOrderAndReturnId();

        mockMvc.perform(get("/order/by-user?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getOrderItems() throws Exception {
        String orderId = createOrderAndReturnId();

        mockMvc.perform(get("/order/items/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateOrderItem() throws Exception {
        String orderId = createOrderAndReturnId();

        Long orderItemId = orderItemRepository.findAll().get(0).getId();

        mockMvc.perform(put("/order/update-item/" + orderItemId)
                        .param("quantity", "5")
                        .param("price", "120"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andExpect(jsonPath("$.price").value(120.0));
    }

    @Test
    void removeOrderItem() throws Exception {
        createOrderAndReturnId();
        Long orderItemId = orderItemRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/order/remove-item/" + orderItemId))
                .andExpect(status().isOk());
    }

    @Test
    void getOrdersByDateRange() throws Exception {
        createOrderAndReturnId();

        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String before = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);

        mockMvc.perform(get("/order/by-date-range")
                        .param("startDate", before)
                        .param("endDate", now)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
