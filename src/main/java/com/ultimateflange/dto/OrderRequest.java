package com.ultimateflange.dto;



import lombok.Data;

@Data
public class OrderRequest {
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
    private String specs;
    private String address;
    private String contactMethod;
    private Double amount;
}