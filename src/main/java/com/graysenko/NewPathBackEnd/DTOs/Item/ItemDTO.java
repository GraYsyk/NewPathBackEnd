package com.graysenko.NewPathBackEnd.DTOs.Item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String frontImage;
    private String backImage;

    private boolean inStock = false;

    private Boolean deleted = false;

    private List<String> images;
    private List<ProductVariantsDTO> variants;

    public ItemDTO(Long id,
                   String name,
                   String description,
                   Double price,
                   String frontImage,
                   String backImage,
                   boolean inStock,
                   Boolean isDeleted,
                   List<String> images,
                   List<ProductVariantsDTO> variants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.frontImage = frontImage;
        this.backImage = backImage;
        this.inStock = inStock;
        this.deleted = isDeleted;
        this.images = images;
        this.variants = variants;
    }
}
