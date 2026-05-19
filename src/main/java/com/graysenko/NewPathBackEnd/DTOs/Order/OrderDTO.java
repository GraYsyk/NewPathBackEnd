package com.graysenko.NewPathBackEnd.DTOs.Order;

import com.graysenko.NewPathBackEnd.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String userEmail;
    private List<OrderItemsDTO> orderItems;
    private OrderStatus orderStatus;
    private Timestamp orderDate;
    private Double totalAmount;
}
