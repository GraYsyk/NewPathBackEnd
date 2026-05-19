package com.graysenko.NewPathBackEnd.DTOs.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderStatusDTO {
    private String orderStatus; // Status "PAID, SHIPPED, etc."
    private Long count;         // Count of orders with this status - 298
}
