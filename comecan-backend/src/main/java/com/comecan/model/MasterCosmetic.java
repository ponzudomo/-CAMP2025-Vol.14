package com.comecan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "master_cosmetics")
public class MasterCosmetic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long masterCosmeticId;

    @Column(nullable = false)
    private String name; 

    private String brand; 

    private String categoryLarge;

    private String categorySmall;

    @Column(unique = true, nullable = false)
    private String janCode; 

    @Column(columnDefinition = "TEXT")
    private String imageUrl; 

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public MasterCosmetic() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getMasterCosmeticId() {
        return masterCosmeticId;
    }

    public void setMasterCosmeticId(Long masterCosmeticId) {
        this.masterCosmeticId = masterCosmeticId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategoryLarge() {
        return categoryLarge;
    }

    public void setCategoryLarge(String categoryLarge) {
        this.categoryLarge = categoryLarge;
    }

    public String getCategorySmall() {
        return categorySmall;
    }

    public void setCategorySmall(String categorySmall) {
        this.categorySmall = categorySmall;
    }

    public String getJanCode() {
        return janCode;
    }

    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}