package com.ultimateflange.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderRef;

    private Long supplierId;

    private String supplierName;

    private String customerEmail;

    private String customerName;

    private String customerCompany;

    private String customerPhone;

    private String productKey;

    private String productName;

    private Integer quantity;

    private String size;

    private String material;

    @Column(length = 1000)
    private String specs;

    @Column(length = 1000)
    private String address;

    @Enumerated(EnumType.STRING)
    private ContactMethod contactMethod;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderRef == null) {
            orderRef = "ORD-" + System.currentTimeMillis();
        }
        if (status == null) {
            status = OrderStatus.pending;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}