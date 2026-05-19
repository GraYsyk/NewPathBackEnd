package com.graysenko.NewPathBackEnd.Entities.Item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double price;

    //TODO: Add Discount

    private String frontImage;
    private String backImage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductVariant> productVariant;

    @Column(nullable = false)
    private boolean deleted = false;

    public Item(String name, String description, int price, List<ProductVariant> productVariant) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.productVariant = productVariant;
    }
}
