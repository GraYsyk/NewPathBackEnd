package com.graysenko.NewPathBackEnd.DTOs.Item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettingsDTO {
    private String collectionName;
    private String collectionDesc;
}
