package com.example.ny.Controller;

import com.example.ny.Service.DiscordService;
import com.example.ny.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // --- CẤU HÌNH THÔNG TIN CƠ BẢN ---
    private final String tenChi = "Bích Loan";
    private final String tenEm = "Anh Đức ny của chị";
    private final String loiNhan = "Em bé đi đường cẩn thận nhaaaa. Anh yêu em. Ký tên: Ngôi nhà nhỏ của emm.\uD83E\uDEF6\n";

    // Email nhận thông báo (cho phần vòng quay may mắn)
    private final String myEmail = "ducdath04243@fpt.edu.vn";

    @Autowired
    private EmailService emailService;

    @Autowired
    private DiscordService discordService;

    // --- CÁC TRANG VIEW ---

    @GetMapping("/")
    public String redirectToLetter() {
        return "redirect:/gui-chi";
    }

    @GetMapping("/gui-chi")
    public String showLetter(Model model) {
        model.addAttribute("tenNguoiNhan", tenChi);
        model.addAttribute("tenNguoiGui", tenEm);
        model.addAttribute("loiNhanYeuThuong", loiNhan);
        return "letter";
    }

    // --- XỬ LÝ GỬI LỜI NHẮN (ĐÃ SỬA SANG DISCORD) ---
    @PostMapping("/gui-chi")
    public ResponseEntity<String> handleReply(
            @RequestParam("reply_message") String replyMessage,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            // Tạo nội dung tin nhắn gửi về Discord
            StringBuilder message = new StringBuilder();
            message.append("💌 **THƯ MỚI TỪ ").append(tenChi.toUpperCase()).append("!** 💌\n");
            message.append("--------------------------------\n");
            message.append("📝 **Nội dung:**\n");
            message.append("> ").append(replyMessage).append("\n");

            // Kiểm tra xem có ảnh không
            if (imageFile != null && !imageFile.isEmpty()) {
                message.append("--------------------------------\n");
                message.append("📸 **Lưu ý:** Chị ấy có gửi kèm một bức ảnh! (Hãy kiểm tra server hoặc folder upload)\n");
            }

            message.append("--------------------------------\n");
            message.append("👉 *Mau vào rep tin nhắn của vợ đi nhé!*");

            // Gửi thông báo qua Discord
            discordService.sendNotification(message.toString());

            return ResponseEntity.ok("Lời nhắn của chị đã được gửi đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Đã có lỗi xảy ra, không thể gửi tin nhắn.");
        }
    }

    // --- CÁC TRANG CHỨC NĂNG KHÁC ---

    @GetMapping("/ghep-hinh")
    public String showPuzzlePage() {
        return "ghep-hinh";
    }

    @GetMapping("/ky-niem")
    public String showAlbumPage() {
        return "ky-niem";
    }

    @GetMapping("/nghe-nhac")
    public String showMusicPage() {
        return "nhac";
    }

    @GetMapping("/dem-ngay")
    public String showCountdownPage() {
        return "dem-ngay";
    }

    // --- PHẦN VÒNG QUAY MAY MẮN ---
    static class PrizeDto {
        private String prize;
        public String getPrize() { return prize; }
        public void setPrize(String prize) { this.prize = prize; }
    }

    @GetMapping("/vong-quay")
    public String showWheelPage() {
        return "vong-quay";
    }

    @PostMapping("/vong-quay/thong-bao")
    @ResponseBody
    public ResponseEntity<String> notifyPrize(@RequestBody PrizeDto prizeDto) {
        try {
            String prize = prizeDto.getPrize();
            // Phần này vẫn giữ Email như cũ (hoặc bạn có thể đổi sang Discord nếu thích)
            String subject = "Chúc mừng! Bạn gái đã quay trúng thưởng!";
            String text = "Bạn gái của bạn vừa quay Vòng Quay May Mắn và đã trúng phần thưởng: \"" + prize + "\".\n\nHãy chuẩn bị thực hiện nhé!";

            emailService.sendSimpleEmail(myEmail, subject, text);

            return ResponseEntity.ok("Thông báo đã được gửi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi gửi thông báo.");
        }
    }

    // --- CÁC MINI GAME KHÁC ---

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
        return "open-when";
    }

    @GetMapping("/timeline")
    public String showTimelinePage() {
        return "timeline";
    }

    @GetMapping("/love-map")
    public String showMapPage() {
        return "love-map";
    }

    @GetMapping("/safe")
    public String showSafePage() {
        return "safe";
    }

    // --- CỬA HÀNG (DISCORD) ---

    @GetMapping("/store")
    public String showStorePage() {
        return "store";
    }

    @PostMapping("/api/buy-item")
    @ResponseBody
    public ResponseEntity<String> buyItem(@RequestParam("itemName") String itemName, @RequestParam("price") int price) {
        try {
            String message = "🚨 **ĐƠN HÀNG MỚI!** 🚨\n" +
                    "--------------------------------\n" +
                    "🎁 **Vật phẩm:** " + itemName + "\n" +
                    "💰 **Giá:** " + price + " Xu\n" +
                    "--------------------------------\n" +
                    "👉 *Anh mau thực hiện yêu cầu của vợ đi nhé!*";

            discordService.sendNotification(message);

            return ResponseEntity.ok("Mua thành công! Đã báo tin qua Discord.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống");
        }
    }

    // --- THỬ THÁCH 30 NGÀY (DISCORD) ---

    @GetMapping("/challenge")
    public String showChallengePage() {
        return "challenge";
    }

    @PostMapping("/api/complete-challenge")
    @ResponseBody
    public ResponseEntity<String> completeChallenge(@RequestParam("day") int day, @RequestParam("msg") String msg) {
        // CẤU HÌNH NGÀY BẮT ĐẦU ĐI QUÂN SỰ
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate today = LocalDate.now();

        LocalDate unlockDate = startDate.plusDays(day - 1);

        if (today.isBefore(unlockDate)) {
            return ResponseEntity.badRequest().body("Chưa đến ngày này đâu bé ơi! Đừng ăn gian nha 😘");
        }

        try {
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

    // --- GIÁNG SINH (DISCORD) ---

    private final LocalDate XMAS_DATE = LocalDate.of(2025, 12, 25);
    private final Map<Integer, String> GIFTS = new HashMap<>() {{
        put(1, "Voucher 150k ở Thành Đô");
        put(2, "Voucher 200k ở Winmart");
        put(3, "Voucher 200k ở Ốc sên");
    }};

    @GetMapping("/christmas")
    public String showChristmasPage() {
        return "christmas";
    }

    @PostMapping("/api/open-gift")
    @ResponseBody
    public ResponseEntity<String> openGift(@RequestParam("boxId") int boxId) {
        LocalDate today = LocalDate.now();

        // Kiểm tra ngày mở quà (Mở comment dòng dưới để test luôn, hoặc để nguyên nếu muốn đúng ngày mới mở)
        // if (today.isBefore(XMAS_DATE)) {
        //    return ResponseEntity.badRequest().body("Ho Ho Ho! Ông già Noel chưa đến! Đợi đến 25/12 nhé bé ngoan 🎅");
        // }

        String giftName = GIFTS.getOrDefault(boxId, "Một nụ hôn nồng cháy");

        try {
            String message = "🎄 **GIÁNG SINH AN LÀNH!** 🎄\n" +
                    "--------------------------------\n" +
                    "🎁 **Vợ đã chọn Hộp quà số:** " + boxId + "\n" +
                    "✨ **Phần thưởng:** " + giftName + "\n" +
                    "--------------------------------\n" +
                    "👉 *Anh hãy chuẩn bị quà để trao tay ngay nhé!*";

            discordService.sendNotification(message);
            return ResponseEntity.ok(giftName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }
}