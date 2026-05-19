package com.graysenko.NewPathBackEnd.DTOs.Order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemsDTO {
    private Long itemId;
    private String itemName;
    private String frontImage;
    private String size;
    private String color;
    private Integer quantity;
    private Double priceAtPurchase;
}
