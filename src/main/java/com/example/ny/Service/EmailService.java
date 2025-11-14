package com.example.ny.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Email đã được xác minh trên Brevo
    private final String FROM_EMAIL = "daod1068@gmail.com";

    // Phương thức mới để gửi email có file đính kèm
    public void sendEmailWithAttachment(String to, String subject, String body, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // SỬA LỖI Ở ĐÂY: Phải dùng email đã xác minh
            helper.setFrom(FROM_EMAIL);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(
                        attachment.getOriginalFilename(),
                        new ByteArrayResource(attachment.getBytes())
                );
            }

            mailSender.send(message);
            System.out.println("Mail sent successfully!");

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // SỬA LỖI Ở ĐÂY: Thêm dòng setFrom
            message.setFrom(FROM_EMAIL);

            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Simple Mail sent successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}