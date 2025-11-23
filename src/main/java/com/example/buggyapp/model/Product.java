package com.example.buggyapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // DEFECT: Using double for monetary values instead of BigDecimal
    private double price;

    private Integer quantity;

    // DEFECT: No validation on description length
    @Column(length = 5000)
    private String description;

    // DEFECT: Dead code - unused field
    private String unusedField;

    // DEFECT: Magic number in annotation
    @Column(length = 100)
    private String category;

    public Product() {
    }

    // DEFECT: Too many parameters in constructor (code smell)
    public Product(Long id, String name, double price, Integer quantity, String description, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.category = category;
    }

    // DEFECT: Duplicate code in getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    // DEFECT: Allows negative prices
    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // DEFECT: Allows negative quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // DEFECT: Method has too high cognitive complexity
    public boolean isAvailable() {
        if (quantity != null) {
            if (quantity > 0) {
                if (price > 0) {
                    if (name != null) {
                        if (!name.isEmpty()) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // DEFECT: Missing @Override annotation
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
