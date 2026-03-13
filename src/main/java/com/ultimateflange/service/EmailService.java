package com.ultimateflange.service;

import com.ultimateflange.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${admin.email}")
    private String adminEmail;

    @Async
    public void sendOrderNotification(Order order) {
        try {

            log.info("Preparing email for order {}", order.getOrderRef());

            // Admin email
            sendEmail(
                    adminEmail,
                    "NEW ORDER - " + order.getOrderRef(),
                    buildAdminEmail(order)
            );

            // Customer email
            if (order.getCustomerEmail() != null) {
                sendEmail(
                        order.getCustomerEmail(),
                        "Order Confirmation - " + order.getOrderRef(),
                        buildCustomerEmail(order)
                );
            }

            log.info("Emails sent successfully");

        } catch (Exception e) {
            log.error("Email sending failed {}", e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);   // ⭐ important fix
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        log.info("Email sent to {}", to);
    }

    private String buildAdminEmail(Order order) {

        return String.format("""
                ==============================
                NEW ORDER RECEIVED
                ==============================

                Order Reference: %s
                Date: %s

                PRODUCT
                Product: %s
                Quantity: %d
                Size: %s
                Material: %s
                Amount: ₹%.2f

                CUSTOMER
                Name: %s
                Email: %s
                Phone: %s
                Company: %s
                """,

                order.getOrderRef(),
                order.getOrderDate(),
                order.getProductName(),
                order.getQuantity(),
                order.getSize(),
                order.getMaterial(),
                order.getAmount(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone(),
                order.getCustomerCompany()
        );
    }

    private String buildCustomerEmail(Order order) {

        return String.format("""
                Dear %s,

                Thank you for your order.

                Order Reference: %s
                Product: %s
                Quantity: %d

                We will contact you within 24 hours.

                Regards
                Ultimate Flange Team
                """,

                order.getCustomerName(),
                order.getOrderRef(),
                order.getProductName(),
                order.getQuantity()
        );
    }
}
