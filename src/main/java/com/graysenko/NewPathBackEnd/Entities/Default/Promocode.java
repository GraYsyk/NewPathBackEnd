package com.graysenko.NewPathBackEnd.Entities.Default;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "promocode")
public class Promocode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true, nullable = false)
    private String code; // UPPER CASE

    private Integer discountPercent;

    private boolean active;

    private Integer usageLimit;

    private Integer usedCount = 0;

    private Timestamp expiresAt; //null - never

    public Promocode(String code, Integer discountPercent, boolean active, Integer usageLimit, Integer usedCount, Timestamp expiresAt) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.active = active;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.expiresAt = expiresAt;
    }
}
