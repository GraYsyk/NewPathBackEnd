package com.graysenko.NewPathBackEnd.DTOs.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyGrowthDTO {
    private String month;
    private Long orders;
}
