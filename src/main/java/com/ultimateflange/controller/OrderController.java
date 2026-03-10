package com.ultimateflange.controller;

;
import com.ultimateflange.dto.ApiResponse;
import com.ultimateflange.dto.OrderRequest;
import com.ultimateflange.model.Order;
import com.ultimateflange.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        try {
            log.info("📦 Received order request for product: {}", request.getProductName());

            Order order = orderService.createOrder(request);

            // Email will be sent automatically by EmailService
            log.info("✅ Order created successfully: {}", order.getOrderRef());

            return ResponseEntity.ok(
                    ApiResponse.success("Order created successfully", order)
            );
        } catch (Exception e) {
            log.error("❌ Order creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Order failed: " + e.getMessage())
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Order>> getSupplierOrders(@PathVariable Long supplierId) {
        return ResponseEntity.ok(orderService.getOrdersBySupplier(supplierId));
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(email));
    }

    @GetMapping("/{orderRef}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderRef) {
        return ResponseEntity.ok(orderService.getOrderByRef(orderRef));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestBody StatusUpdateRequest request) {
        try {
            Order order = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(
                    ApiResponse.success("Status updated", order)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }
}

class StatusUpdateRequest {
    private String status;
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}