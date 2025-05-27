package com.micro.product_service.persistence.repository;

import com.dto_common.OrderItemDTO;
import com.micro.product_service.persistence.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {

    @Query("SELECT new com.dto_common.OrderItemDTO(i.id,i.quantity,i.price,i.product.id , i.order.id) " +
            "FROM OrderItem i WHERE i.order.id = :orderId")
    List<OrderItemDTO> findByOrderId(@Param("orderId") Long orderId);
}
