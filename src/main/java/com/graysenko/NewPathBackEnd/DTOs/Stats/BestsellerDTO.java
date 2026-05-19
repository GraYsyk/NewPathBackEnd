package com.graysenko.NewPathBackEnd.DTOs.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestsellerDTO {
    private Long itemId;
    private String name;
    private String frontImage;
    private Long totalSold;
}
