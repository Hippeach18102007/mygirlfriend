package com.example.ny.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GeminiService {
    private final String API_KEY = "DÁN_MÃ_API_CỦA_BẠN_VÀO_ĐÂY";
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    public String chatWithGemini(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        String systemInstruction = "Bạn là trợ lý tình yêu ảo của một cặp đôi tên là Đức và Vợ. " +
                "Hãy trả lời một cách cute, hài hước, và hơi sến súa một chút. " +
                "Luôn gọi người dùng là 'Chị đẹp' hoặc 'Cô chủ nhỏ'. " +
                "Nếu cô ấy than vãn về người yêu, hãy an ủi cô ấy nhưng nhớ bênh vực anh Đức một tí khéo léo.";

        // 2. Tạo cấu trúc JSON gửi đi (Hơi loằng ngoằng vì Google yêu cầu thế)
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        // Gộp hướng dẫn hệ thống + tin nhắn người dùng
        part.put("text", systemInstruction + "\n\nNgười dùng nói: " + userMessage);

        content.put("parts", Collections.singletonList(part));
        requestBody.put("contents", Collections.singletonList(content));

        // 3. Gửi Request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, Map.class);

            // 4. Bóc tách dữ liệu trả về để lấy câu trả lời
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                Map<String, Object> contentRes = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> partsRes = (List<Map<String, Object>>) contentRes.get("parts");
                return (String) partsRes.get(0).get("text");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Úi, AI đang bị đau đầu (Lỗi server). Thử lại sau nhé!";
        }
        return "Hic, không hiểu sao mình không trả lời được...";
    }
}