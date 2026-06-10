package com.virtualcafe.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cafe_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_each", nullable = false)
    private double priceEach;

    @Column(nullable = false)
    private String status; // PENDING, IN_PROGRESS, SERVED, CANCELLED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Order() {
        this.createdAt = Instant.now();
        this.status = "PENDING";
    }

    public Order(String customerName, String item, int quantity, double priceEach) {
        this.customerName = customerName;
        this.item = item;
        this.quantity = quantity;
        this.priceEach = priceEach;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(double priceEach) {
        this.priceEach = priceEach;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
