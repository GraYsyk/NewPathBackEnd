package com.graysenko.NewPathBackEnd.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private Long itemId;
    private String name;
    private Integer quantity;
    private Integer stockQuantity;
    private String size;
    private String color;
    private Double price;
    private String frontImage;
}
