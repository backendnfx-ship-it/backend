package com.ultimateflange.service;

import com.ultimateflange.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${admin.email}")
    private String adminEmail;

    public void sendOrderNotification(Order order) {
        try {
            log.info("📧 Preparing to send order notification email to: {}", adminEmail);
            log.info("📧 SMTP Host: smtp.gmail.com, Port: 587");
            log.info("📧 Username: {}", adminEmail);

            sendEmailToAdmin(order);
            sendEmailToCustomer(order);

            log.info("✅ All emails sent successfully!");

        } catch (MailAuthenticationException e) {
            log.error("❌ Gmail authentication failed! App password sahi hai?");
            log.error("❌ Error: {}", e.getMessage());
            log.error("✅ Solution: Google Account → Security → App Passwords generate karo");
        } catch (MailSendException e) {
            log.error("❌ Mail server connection failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error: {}", e.getMessage());
        }
    }

    private void sendEmailToAdmin(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("🛒 NEW ORDER RECEIVED - " + order.getOrderRef());
            message.setText(buildAdminEmailBody(order));

            mailSender.send(message);
            log.info("✅ Admin notification sent to: {}", adminEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send admin email: {}", e.getMessage());
            throw e;
        }
    }

    private void sendEmailToCustomer(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getCustomerEmail());
            message.setSubject("✅ Order Confirmation - " + order.getOrderRef());
            message.setText(buildCustomerEmailBody(order));

            mailSender.send(message);
            log.info("✅ Customer confirmation sent to: {}", order.getCustomerEmail());

        } catch (Exception e) {
            log.error("❌ Failed to send customer email: {}", e.getMessage());
        }
    }

    private String buildAdminEmailBody(Order order) {
        return String.format("""
            ========================================
            🔔 NEW ORDER RECEIVED
            ========================================
            
            Order Reference: %s
            Order Date: %s
            Status: %s
            
            📦 PRODUCT DETAILS:
            Product: %s
            Quantity: %d
            Size: %s
            Material: %s
            Amount: ₹%.2f
            
            👤 CUSTOMER DETAILS:
            Name: %s
            Email: %s
            Phone: %s
            Company: %s
            
            ========================================
            """,
                order.getOrderRef(),
                order.getOrderDate(),
                order.getStatus(),
                order.getProductName(),
                order.getQuantity(),
                order.getSize() != null ? order.getSize() : "N/A",
                order.getMaterial() != null ? order.getMaterial() : "N/A",
                order.getAmount() != null ? order.getAmount() : 0.0,
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone() != null ? order.getCustomerPhone() : "N/A",
                order.getCustomerCompany() != null ? order.getCustomerCompany() : "N/A"
        );
    }

    private String buildCustomerEmailBody(Order order) {
        return String.format("""
            Dear %s,
            
            Thank you for your order!
            
            Order Reference: %s
            Product: %s
            Quantity: %d
            
            The supplier will contact you within 24 hours.
            
            Regards,
            Ultimate Flange Team
            """,
                order.getCustomerName(),
                order.getOrderRef(),
                order.getProductName(),
                order.getQuantity()
        );
    }
}