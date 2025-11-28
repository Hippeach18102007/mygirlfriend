package com.example.ny.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordService {

    // Dán cái Webhook URL bạn vừa copy ở Discord vào đây
    private final String WEBHOOK_URL = "https://discord.com/api/webhooks/1443940686008160327/b9Vu051IiIV2ZjbF7ltykSAWXOBKWf1-ri6HjQ_5vGB30ynCjMaurNULP2whSwhKHs8w";

    public void sendNotification(String message) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Tạo nội dung JSON để gửi sang Discord
            // Discord yêu cầu định dạng: { "content": "Nội dung tin nhắn" }
            Map<String, String> body = new HashMap<>();
            body.put("content", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(WEBHOOK_URL, request, String.class);

            System.out.println("Đã gửi thông báo Discord thành công!");
        } catch (Exception e) {
            System.out.println("Lỗi gửi Discord: " + e.getMessage());
        }
    }
}