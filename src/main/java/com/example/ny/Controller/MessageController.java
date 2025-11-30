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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }
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

    @GetMapping("/challenge")
    public String showChallengePage() {
        return "challenge"; // Trả về file challenge.html
    }

    @PostMapping("/api/complete-challenge")
    @ResponseBody
    public ResponseEntity<String> completeChallenge(@RequestParam("day") int day, @RequestParam("msg") String msg) {
        // CẤU HÌNH NGÀY BẮT ĐẦU ĐI QUÂN SỰ
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate today = LocalDate.now();

        // Tính ngày được phép mở
        LocalDate unlockDate = startDate.plusDays(day - 1);

        // Kiểm tra xem đã đến ngày đó chưa
        if (today.isBefore(unlockDate)) {
            return ResponseEntity.badRequest().body("Chưa đến ngày này đâu bé ơi! Đừng ăn gian nha 😘");
        }

        try {
            // Logic tiêu đề tin nhắn khác biệt cho 8 ngày cuối
            String title = "🎖️ **BÁO CÁO TỪ HẬU PHƯƠNG!**";
            if (day >= 23) {
                title = "🚨 **[QUÂN SỰ] TIN KHẨN CẤP!** 🚨";
            }

            String message = title + "\n" +
                    "--------------------------------\n" +
                    "📅 **Ngày thứ:** " + day + "/30\n" +
                    "✅ **Nhiệm vụ:** " + msg + "\n" +
                    "💬 **Trạng thái:** Đã hoàn thành nhiệm vụ!\n" +
                    "--------------------------------\n" +
                    "👉 *Mong anh sớm về!*";

            discordService.sendNotification(message);
            return ResponseEntity.ok("Giỏi lắm! Anh đã nhận được tín hiệu ở đơn vị rồi ❤️");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }
    private final LocalDate XMAS_DATE = LocalDate.of(2025, 12, 25);

    // Danh sách phần quà bên trong các hộp (Bạn tự sửa nhé)
    private final Map<Integer, String> GIFTS = new HashMap<>() {{
        put(1, "Voucher 150k ở Thành Đô");
        put(2, "Voucher 200k ở Winmart");
        put(3, "Voucher 200k ở Ôsc sên");
    }};

    @GetMapping("/christmas")
    public String showChristmasPage() {
        return "christmas"; // Trả về file christmas.html
    }

    @PostMapping("/api/open-gift")
    @ResponseBody
    public ResponseEntity<String> openGift(@RequestParam("boxId") int boxId) {
        LocalDate today = LocalDate.now();

        // 1. Kiểm tra ngày
        if (today.isBefore(XMAS_DATE)) {
            return ResponseEntity.badRequest().body("Ho Ho Ho! Ông già Noel chưa đến! Đợi đến 25/12 nhé bé ngoan 🎅");
        }

        // 2. Lấy tên món quà
        String giftName = GIFTS.getOrDefault(boxId, "Một nụ hôn nồng cháy");

        try {
            // 3. Gửi thông báo Discord
            String message = "🎄 **GIÁNG SINH AN LÀNH!** 🎄\n" +
                    "--------------------------------\n" +
                    "🎁 **Vợ đã chọn Hộp quà số:** " + boxId + "\n" +
                    "✨ **Phần thưởng:** " + giftName + "\n" +
                    "--------------------------------\n" +
                    "👉 *Anh hãy chuẩn bị quà để trao tay ngay nhé!*";

            discordService.sendNotification(message);

            // Trả về tên món quà để hiện lên màn hình
            return ResponseEntity.ok(giftName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }
}