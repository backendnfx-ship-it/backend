package com.ultimateflange.service;

import com.ultimateflange.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${admin.email:mushaabkhan894@gmail.com}")
    private String adminEmail;
    
    @Async  // Email background mein bhejo - fast response ke liye
    public void sendOrderNotification(Order order) {
        try {
            log.info("📧 Preparing email for order: {}", order.getOrderRef());
            log.info("📧 Admin email: {}", adminEmail);
            log.info("📧 SMTP Host: smtp.gmail.com, Port: 587");
            log.info("📧 Username: mushaabkhan894@gmail.com");
            
            // Admin email
            sendEmail(
                adminEmail,
                "🛒 NEW ORDER - " + order.getOrderRef(),
                buildAdminEmail(order)
            );
            
            // Customer email
            sendEmail(
                order.getCustomerEmail(),
                "✅ Order Confirmation - " + order.getOrderRef(),
                buildCustomerEmail(order)
            );
            
            log.info("✅ Emails sent successfully!");
            
        } catch (MailAuthenticationException e) {
            log.error("❌ Gmail AUTHENTICATION FAILED! App password sahi nahi hai!");
            log.error("Error: {}", e.getMessage());
            log.error("✅ Solution: App password mein SPACES nahi hone chahiye!");
            log.error("✅ Example: 'abcd efgh ijkl mnop' → 'abcdefghijklmnop'");
        } catch (MailSendException e) {
            log.error("❌ Mail SEND failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error: {}", e.getMessage());
        }
    }
    
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("✅ Email sent to: {}", to);
            
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
            throw e;
        }
    }
    
    private String buildAdminEmail(Order order) {
        return String.format("""
            ========================================
            🛒 NEW ORDER RECEIVED
            ========================================
            
            Order Reference: %s
            Date: %s
            
            PRODUCT:
            Product: %s
            Quantity: %d
            Size: %s
            Material: %s
            Amount: ₹%.2f
            
            CUSTOMER:
            Name: %s
            Email: %s
            Phone: %s
            Company: %s
            
            ========================================
            """,
            order.getOrderRef(),
            order.getOrderDate(),
            order.getProductName(),
            order.getQuantity(),
            order.getSize() != null ? order.getSize() : "N/A",
            order.getMaterial() != null ? order.getMaterial() : "N/A",
            order.getAmount() != null ? order.getAmount() : 0,
            order.getCustomerName(),
            order.getCustomerEmail(),
            order.getCustomerPhone() != null ? order.getCustomerPhone() : "N/A",
            order.getCustomerCompany() != null ? order.getCustomerCompany() : "N/A"
        );
    }
    
    private String buildCustomerEmail(Order order) {
        return String.format("""
            Dear %s,
            
            Thank you for your order!
            
            Order Reference: %s
            Product: %s
            Quantity: %d
            
            Your order has been sent to %s.
            They will contact you within 24 hours.
            
            Regards,
            Ultimate Flange Team
            """,
            order.getCustomerName(),
            order.getOrderRef(),
            order.getProductName(),
            order.getQuantity(),
            order.getSupplierName()
        );
    }
}
