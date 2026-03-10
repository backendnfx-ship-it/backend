package com.ultimateflange.repository;

import com.ultimateflange.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySupplierId(Long supplierId);
    List<Order> findByCustomerEmail(String customerEmail);
    Order findByOrderRef(String orderRef);
    List<Order> findByStatus(String status);
}