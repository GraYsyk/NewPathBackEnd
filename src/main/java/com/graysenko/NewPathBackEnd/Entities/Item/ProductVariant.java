package com.graysenko.NewPathBackEnd.Entities.Item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String size;
    private String color;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Item product;

    public ProductVariant(String size, String color, int quantity, Item product) {
        this.size = size;
        this.color = color;
        this.quantity = quantity;
        this.product = product;
    }
}
