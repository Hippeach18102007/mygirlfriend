package com.example.ny.Controller;


import com.example.ny.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MessageController {
    @GetMapping("/")
    public String redirectToLetter() {
        return "redirect:/gui-chi";
    }

    private final String tenChi = "Bích Loan";
    private final String tenEm = "Anh Đức ny của chị";
    private final String loiNhan = "Day 3: Dùng APP. " +
            "Hallo tềnh yêu:)). Nay ko có j mấy ngoại trừ việc em đau bụng vãi nho. Ông kia đấm đau đét:).";

    @Value("${spring.mail.username}")
    private String myEmail;

    @Autowired
    private EmailService emailService;

    @GetMapping("/gui-chi")
    public String showLetter(Model model) {
        model.addAttribute("tenNguoiNhan", tenChi);
        model.addAttribute("tenNguoiGui", tenEm);
        model.addAttribute("loiNhanYeuThuong", loiNhan);
        return "letter";
    }

    // Cập nhật phương thức POST
    @PostMapping("/gui-chi")
    public ResponseEntity<String> handleReply(
            @RequestParam("reply_message") String replyMessage,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            String subject = "Có lời nhắn từ " + tenChi + "!";
            String body = "Chị " + tenChi + " đã gửi lời nhắn cho bạn:\n\n\"" + replyMessage + "\"";

            emailService.sendEmailWithAttachment(myEmail, subject, body, imageFile);

            return ResponseEntity.ok("Lời nhắn của chị đã được gửi đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Đã có lỗi xảy ra, không thể gửi tin nhắn.");
        }
    }
    @GetMapping("/ky-niem")
    public String showAlbumPage() {
        return "ky-niem"; // Trả về file ky-niem.html trong thư mục templates
    }
    @GetMapping("/nghe-nhac")
    public String showMusicPage() {
        return "nhac"; // Trả về file nhac.html
    }
    @GetMapping("/dem-ngay")
    public String showCountdownPage() {
        return "dem-ngay"; // Trả về file dem-ngay.html
    }
}