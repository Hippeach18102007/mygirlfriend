package com.example.ny.Service;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Phương thức mới để gửi email có file đính kèm
    public void sendEmailWithAttachment(String to, String subject, String body, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = multipart message (cho phép đính kèm file)
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("no-reply@yourdomain.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            // Kiểm tra xem có file đính kèm không
            if (attachment != null && !attachment.isEmpty()) {
                // Thêm file đính kèm
                helper.addAttachment(
                        attachment.getOriginalFilename(), // Tên file
                        new ByteArrayResource(attachment.getBytes()) // Dữ liệu file
                );
            }

            mailSender.send(message);
            System.out.println("Mail sent successfully!");

        } catch (Exception e) {
            // Ném ngoại lệ để Controller có thể bắt và xử lý
            throw new RuntimeException("Failed to send email", e);
        }
    }
}