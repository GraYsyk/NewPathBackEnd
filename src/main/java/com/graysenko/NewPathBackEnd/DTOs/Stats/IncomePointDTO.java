package com.graysenko.NewPathBackEnd.DTOs.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncomePointDTO {    //Giving back a List of this DTO. Front is getting array of dots
    private String label;
    private Double income;
}
