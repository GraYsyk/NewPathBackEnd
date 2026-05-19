package com.graysenko.NewPathBackEnd.DTOs.Item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ProductVariantsDTO {

    private String size;
    private String color;
    private int quantity;

    public ProductVariantsDTO(String size, String color, int quantity) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
    }
}
