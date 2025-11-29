package com.example.ny.Controller;


import com.example.ny.Service.DiscordService;
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
    private final String loiNhan = "Em bé đi đường cẩn thận nhaaaa. Anh yêu em. Ký tên: Ngôi nhà nhỏ của emm.\uD83E\uDEF6\n";

    // --- ĐÃ XÓA BIẾN "myEmail" LẤY TỪ @Value ---

    @Autowired
    private EmailService emailService;
    @Autowired
    private DiscordService discordService;
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

            // --- SỬA LỖI Ở ĐÂY ---
            // Chỉ định rõ email nhận thư, không dùng @Value
            String emailTo = "ducdath04243@fpt.edu.vn";

            emailService.sendEmailWithAttachment(emailTo, subject, body, imageFile);

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
    @GetMapping("/mon-an")
    public String showFoodPickerPage() {
        return "mon-an";
    }
    @GetMapping("/bai-hoc")
    public String showLessonsPage() {
        return "bai-hoc";
    }
    @GetMapping("/tro-choi-nho")
    public String showMemoryGamePage() {
        return "tro-choi-nho";
    }
    @GetMapping("/trac-nghiem")
    public String showQuizPage() {
        return "trac-nghiem";
    }
    @GetMapping("/truth-or-dare")
    public String showTruthOrDarePage() {
        return "truth-or-dare";
    }
    @GetMapping("/thoi-tiet")
    public String showWeatherPage() {
        return "thoi-tiet";
    }
    @GetMapping("/open-when")
    public String showOpenWhenPage() {
        return "open-when"; // Trả về file open-when.html
    }
    @GetMapping("/timeline")
    public String showTimelinePage() {
        return "timeline"; // Trả về file timeline.html
    }
    @GetMapping("/love-map")
    public String showMapPage() {
        return "love-map"; // Trả về file love-map.html
    }
    @GetMapping("/safe")
    public String showSafePage() {
        return "safe"; // Trả về file safe.html
    }
    @GetMapping("/store")
    public String showStorePage() {
        return "store";
    }

    @PostMapping("/api/buy-item")
    @ResponseBody
    public ResponseEntity<String> buyItem(@RequestParam("itemName") String itemName, @RequestParam("price") int price) {
        try {
            // Nội dung tin nhắn (Discord hỗ trợ icon rất đẹp)
            String message = "🚨 **ĐƠN HÀNG MỚI!** 🚨\n" +
                    "--------------------------------\n" +
                    "🎁 **Vật phẩm:** " + itemName + "\n" +
                    "💰 **Giá:** " + price + " Xu\n" +
                    "--------------------------------\n" +
                    "👉 *Anh mau thực hiện yêu cầu của vợ đi nhé!*";

            // Gửi qua Discord
            discordService.sendNotification(message);

            return ResponseEntity.ok("Mua thành công! Đã báo tin qua Discord.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống");
        }
    }
}