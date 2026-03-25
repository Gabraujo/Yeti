package com.example.yeti.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "batches", nullable = false)
    private int batches;

    @Column(name = "qty_per_batch", nullable = false)
    private int quantityPerBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    protected Item() {
        // JPA only
    }

    public Item(String code, String description, int batches, int quantityPerBatch, Sector sector) {
        this.code = code;
        this.description = description;
        this.batches = batches;
        this.quantityPerBatch = quantityPerBatch;
        this.sector = sector;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getBatches() {
        return batches;
    }

    public int getQuantityPerBatch() {
        return quantityPerBatch;
    }

    public Sector getSector() {
        return sector;
    }

    public void update(String code, String description, int batches, int quantityPerBatch) {
        this.code = code;
        this.description = description;
        this.batches = batches;
        this.quantityPerBatch = quantityPerBatch;
    }
}
