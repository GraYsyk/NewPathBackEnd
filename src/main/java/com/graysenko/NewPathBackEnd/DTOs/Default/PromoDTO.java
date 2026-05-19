package com.graysenko.NewPathBackEnd.DTOs.Default;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromoDTO {
    private Long id;
    private String code;
    private int discountPercent;
    private int usageLimit;
    private int usedCount;
    private Long expiresAt;
    private boolean active;
}
