package com.ultimateflange.service;

import com.ultimateflange.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    public void sendOrderNotification(Order order) {

        try {

            log.info("📧 Preparing email for order {}", order.getOrderRef());

            // Admin email
            sendEmail(
                    adminEmail,
                    "NEW ORDER - " + order.getOrderRef(),
                    buildAdminEmail(order)
            );

            // Customer email
            if (order.getCustomerEmail() != null && !order.getCustomerEmail().isEmpty()) {

                sendEmail(
                        order.getCustomerEmail(),
                        "Order Confirmation - " + order.getOrderRef(),
                        buildCustomerEmail(order)
                );

            }

            log.info("✅ Emails sent successfully");

        } catch (Exception e) {

            log.error("❌ Email sending failed: {}", e.getMessage());
            e.printStackTrace();

        }

    }

    private void sendEmail(String to, String subject, String text) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("✅ Email sent to {}", to);

        } catch (Exception e) {

            log.error("❌ Failed to send email to {} : {}", to, e.getMessage());
            throw e;

        }

    }

    private String buildAdminEmail(Order order) {

        return String.format(
                "NEW ORDER RECEIVED\n\n" +
                "Order Reference: %s\n" +
                "Date: %s\n\n" +
                "PRODUCT\n" +
                "Product: %s\n" +
                "Quantity: %d\n" +
                "Size: %s\n" +
                "Material: %s\n" +
                "Amount: ₹%.2f\n\n" +
                "CUSTOMER\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Phone: %s\n" +
                "Company: %s",

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

        return String.format(
                "Dear %s,\n\n" +
                "Thank you for your order.\n\n" +
                "Order Reference: %s\n" +
                "Product: %s\n" +
                "Quantity: %d\n\n" +
                "We will contact you within 24 hours.\n\n" +
                "Regards\n" +
                "Ultimate Flange Team",

                order.getCustomerName(),
                order.getOrderRef(),
                order.getProductName(),
                order.getQuantity()
        );
    }
}
