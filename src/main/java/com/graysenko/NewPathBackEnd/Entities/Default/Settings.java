package com.graysenko.NewPathBackEnd.Entities.Default;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "settings")
public class Settings {

    @Id
    private Long Id = 1L;

    @Column(nullable = false, name = "collection_name")
    private String collectionName;
    @Column(name = "collection_description")
    private String collectionDesc;
    @Column(name = "collection_visible")
    private boolean collectionVisible = true;
    private Timestamp scheduleDropAt;

    public Settings(String collectionName, String collectionDesc, boolean collectionVisible, Timestamp scheduleDropAt) {
        this.collectionName = collectionName;
        this.collectionDesc = collectionDesc;
        this.collectionVisible = collectionVisible;
        this.scheduleDropAt = scheduleDropAt;
    }
}
