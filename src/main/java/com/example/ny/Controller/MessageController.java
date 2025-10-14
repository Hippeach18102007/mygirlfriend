package com.example.ny.Controller;


import com.example.ny.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MessageController {
    @GetMapping("/")
    public String redirectToLetter() {
        return "redirect:/gui-chi";
    }

    private final String tenChi = "Bích Loan";
    private final String tenEm = "Anh Đức ny của chị";
    private final String loiNhan = "Day 6: 4 ngày trước khi anh lên làm người lớn:))). " +
            "Tối nay mệt khum em. Nay anh xem tiktok thấy người ta toàn cầu hôn nhau. Nghe bảo giá vàng đang tăng nên đua nhau đi cưới. Không biết khi nào tới lượt mình:))). Khi tương lai mơ hồ đầy ô trống còn người ta địa vị thành công:)))) chết rồi hiệu ứng tóp tóp phải xóa thôi:))) ";

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
    @GetMapping("/ghep-hinh")
    public String showPuzzlePage() {
        return "ghep-hinh";
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
    static class PrizeDto {
        private String prize;
        public String getPrize() { return prize; }
        public void setPrize(String prize) { this.prize = prize; }
    }
     // Đảm bảo bạn đã tiêm EmailService

    @GetMapping("/vong-quay")
    public String showWheelPage() {
        return "vong-quay";
    }

    @PostMapping("/vong-quay/thong-bao")
    @ResponseBody // Rất quan trọng, để trả về dữ liệu thay vì một trang HTML
    public ResponseEntity<String> notifyPrize(@RequestBody PrizeDto prizeDto) {
        try {
            String prize = prizeDto.getPrize();
            String emailTo = "ducdath04243@fpt.edu.vn"; // <-- THAY EMAIL CỦA BẠN VÀO ĐÂY
            String subject = "Chúc mừng! Bạn gái đã quay trúng thưởng!";
            String text = "Bạn gái của bạn vừa quay Vòng Quay May Mắn và đã trúng phần thưởng: \"" + prize + "\".\n\nHãy chuẩn bị thực hiện nhé!";

            emailService.sendSimpleEmail(emailTo, subject, text);

            return ResponseEntity.ok("Thông báo đã được gửi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi email.");
        }
    }
}