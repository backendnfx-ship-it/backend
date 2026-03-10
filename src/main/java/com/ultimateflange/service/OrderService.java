package com.ultimateflange.service;


import com.ultimateflange.dto.OrderRequest;
import com.ultimateflange.model.Order;

import com.ultimateflange.model.OrderStatus;
import com.ultimateflange.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating new order for product: {}", request.getProductName());

        Order order = new Order();
        order.setSupplierId(request.getSupplierId());
        order.setSupplierName(request.getSupplierName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerCompany(request.getCustomerCompany());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setProductKey(request.getProductKey());
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setSize(request.getSize());
        order.setMaterial(request.getMaterial());
        order.setSpecs(request.getSpecs());
        order.setAddress(request.getAddress());
        order.setAmount(request.getAmount());
        order.setStatus(OrderStatus.pending);

        Order savedOrder = orderRepository.save(order);
        log.info("✅ Order saved with ID: {}, Reference: {}", savedOrder.getId(), savedOrder.getOrderRef());

        // 📧 SEND EMAIL NOTIFICATION TO ADMIN (mushaabkhan894@gmail.com)
        emailService.sendOrderNotification(savedOrder);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersBySupplier(Long supplierId) {
        return orderRepository.findBySupplierId(supplierId);
    }

    public List<Order> getOrdersByCustomer(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    public Order getOrderByRef(String orderRef) {
        return orderRepository.findByOrderRef(orderRef);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status));
        return orderRepository.save(order);
    }
}