package com.example.ny.Controller;

import com.example.ny.Service.DiscordService;
import com.example.ny.Service.EmailService;
import jakarta.servlet.http.HttpSession; // Nhớ import cái này
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
    private final String loiNhan = "1 con tuần lộc, anh yêu emmmmmm \uD83E\uDEF6\n";
    private final String myEmail = "ducdath04243@fpt.edu.vn";

    // 🔥 MẬT KHẨU ĐỂ VÀO TRANG (Bạn sửa ở đây nhé)
    private final String PASSWORD = "21072006";

    @Autowired
    private EmailService emailService;

    @Autowired
    private DiscordService discordService;

    // --- 1. LOGIC ĐĂNG NHẬP / TRANG CHỦ ---

    @GetMapping("/")
    public String index(HttpSession session) {
        // Nếu đã đăng nhập thì vào thẳng thư, chưa thì về login
        if (session.getAttribute("isLoggedIn") != null) {
            return "redirect:/gui-chi";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("password") String password, HttpSession session, Model model) {
        if (PASSWORD.equals(password)) {
            // Đăng nhập thành công -> Lưu vào session
            session.setAttribute("isLoggedIn", true);
            return "redirect:/gui-chi";
        } else {
            // Sai mật khẩu
            model.addAttribute("error", "Sai mật khẩu rồi! Gợi ý: Ngày sinh nhật hoặc kỷ niệm 🎂");
            return "login";
        }
    }

    // Chức năng đăng xuất (nếu cần)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // --- 2. TRANG THƯ (ĐÃ ĐƯỢC BẢO VỆ) ---

    @GetMapping("/gui-chi")
    public String showLetter(Model model, HttpSession session) {
        // 🔥 KIỂM TRA BẢO MẬT: Nếu chưa đăng nhập -> Đá về trang login
        if (session.getAttribute("isLoggedIn") == null) {
            return "redirect:/login";
        }

        model.addAttribute("tenNguoiNhan", tenChi);
        model.addAttribute("tenNguoiGui", tenEm);
        model.addAttribute("loiNhanYeuThuong", loiNhan);
        return "letter";
    }

    // --- CÁC API VÀ TRANG KHÁC (GIỮ NGUYÊN) ---
    // (Lưu ý: Các trang game bên dưới mình không chặn password để chị ấy có thể gửi link game cho bạn xem nếu muốn.
    // Nếu muốn chặn tất cả, bạn phải thêm đoạn check session vào từng hàm @GetMapping)

    @PostMapping("/gui-chi")
    public ResponseEntity<String> handleReply(
            @RequestParam("reply_message") String replyMessage,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            StringBuilder message = new StringBuilder();
            message.append("💌 **THƯ MỚI TỪ ").append(tenChi.toUpperCase()).append("!** 💌\n");
            message.append("--------------------------------\n");
            message.append("📝 **Nội dung:**\n");
            message.append("> ").append(replyMessage).append("\n");

            if (imageFile != null && !imageFile.isEmpty()) {
                message.append("--------------------------------\n");
                message.append("📸 **Lưu ý:** Chị ấy có gửi kèm một bức ảnh!\n");
            }

            message.append("--------------------------------\n");
            message.append("👉 *Mau vào rep tin nhắn của vợ đi nhé!*");

            discordService.sendNotification(message.toString());

            return ResponseEntity.ok("Lời nhắn của chị đã được gửi đi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Đã có lỗi xảy ra.");
        }
    }

    @GetMapping("/ghep-hinh") public String showPuzzlePage() { return "ghep-hinh"; }
    @GetMapping("/ky-niem") public String showAlbumPage() { return "ky-niem"; }
    @GetMapping("/nghe-nhac") public String showMusicPage() { return "nhac"; }
    @GetMapping("/dem-ngay") public String showCountdownPage() { return "dem-ngay"; }

    static class PrizeDto {
        private String prize;
        public String getPrize() { return prize; }
        public void setPrize(String prize) { this.prize = prize; }
    }

    @GetMapping("/vong-quay") public String showWheelPage() { return "vong-quay"; }

    @PostMapping("/vong-quay/thong-bao")
    @ResponseBody
    public ResponseEntity<String> notifyPrize(@RequestBody PrizeDto prizeDto) {
        try {
            emailService.sendSimpleEmail(myEmail, "Trúng thưởng!", "Bạn gái trúng: " + prizeDto.getPrize());
            return ResponseEntity.ok("Thông báo đã được gửi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi.");
        }
    }

    @GetMapping("/mon-an") public String showFoodPickerPage() { return "mon-an"; }
    @GetMapping("/bai-hoc") public String showLessonsPage() { return "bai-hoc"; }
    @GetMapping("/tro-choi-nho") public String showMemoryGamePage() { return "tro-choi-nho"; }
    @GetMapping("/trac-nghiem") public String showQuizPage() { return "trac-nghiem"; }
    @GetMapping("/truth-or-dare") public String showTruthOrDarePage() { return "truth-or-dare"; }
    @GetMapping("/thoi-tiet") public String showWeatherPage() { return "thoi-tiet"; }
    @GetMapping("/open-when") public String showOpenWhenPage() { return "open-when"; }
    @GetMapping("/timeline") public String showTimelinePage() { return "timeline"; }
    @GetMapping("/love-map") public String showMapPage() { return "love-map"; }
    @GetMapping("/safe") public String showSafePage() { return "safe"; }
    @GetMapping("/store") public String showStorePage() { return "store"; }

    @PostMapping("/api/buy-item")
    @ResponseBody
    public ResponseEntity<String> buyItem(@RequestParam("itemName") String itemName, @RequestParam("price") int price) {
        try {
            String message = "🚨 **ĐƠN HÀNG MỚI!**\n🎁 " + itemName + " - 💰 " + price + " Xu";
            discordService.sendNotification(message);
            return ResponseEntity.ok("Mua thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống");
        }
    }

    @GetMapping("/challenge") public String showChallengePage() { return "challenge"; }

    @PostMapping("/api/complete-challenge")
    @ResponseBody
    public ResponseEntity<String> completeChallenge(@RequestParam("day") int day, @RequestParam("msg") String msg) {
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate today = LocalDate.now();
        LocalDate unlockDate = startDate.plusDays(day - 1);

        if (today.isBefore(unlockDate)) {
            return ResponseEntity.badRequest().body("Chưa đến ngày này đâu bé ơi! Đừng ăn gian nha 😘");
        }
        try {
            String message = "🎖️ **BÁO CÁO:** Ngày " + day + " - " + msg;
            discordService.sendNotification(message);
            return ResponseEntity.ok("Giỏi lắm!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }

    private final LocalDate XMAS_DATE = LocalDate.of(2025, 12, 25);
    private final Map<Integer, String> GIFTS = new HashMap<>() {{
        put(1, "Voucher 150k ở Thành Đô");
        put(2, "Voucher 200k ở Winmart");
        put(3, "Voucher 200k ở Ốc sên");
    }};

    @GetMapping("/christmas") public String showChristmasPage() { return "christmas"; }

    @PostMapping("/api/open-gift")
    @ResponseBody
    public ResponseEntity<String> openGift(@RequestParam("boxId") int boxId) {
        String giftName = GIFTS.getOrDefault(boxId, "Một nụ hôn nồng cháy");
        try {
            discordService.sendNotification("🎄 **GIÁNG SINH:** Vợ chọn hộp " + boxId + " - Quà: " + giftName);
            return ResponseEntity.ok(giftName);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }

    @GetMapping("/game-kho") public String showHardGamePage() { return "game-kho"; }
    @GetMapping("/game-2048") public String show2048Game() { return "game-2048"; }
    @GetMapping("/game-piano") public String showPianoGame() { return "game-piano"; }
    @GetMapping("/game-snake") public String showSnakeGame() { return "game-snake"; }
    @GetMapping("/garden") public String showGardenPage() { return "garden"; }

    @PostMapping("/api/water-plant")
    @ResponseBody
    public ResponseEntity<String> waterPlant(@RequestParam("dayCount") int dayCount) {
        try {
            discordService.sendNotification("🌱 **VƯỜN CÂY:** Đã tưới nước! Cấp độ: " + dayCount);
            return ResponseEntity.ok("Đã tưới nước thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }

    @GetMapping("/pharmacy") public String showPharmacyPage() { return "pharmacy"; }

    @PostMapping("/api/ke-don")
    @ResponseBody
    public ResponseEntity<String> prescribeMedicine(@RequestParam("symptom") String symptom) {
        try {
            discordService.sendNotification("🚑 **BỆNH ÁN:** Triệu chứng: " + symptom);
            return ResponseEntity.ok("Đã kê đơn!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống");
        }
    }

    @GetMapping("/heart-game") public String showHeartGamePage() { return "heart-game"; }

    @PostMapping("/api/submit-score")
    @ResponseBody
    public ResponseEntity<String> submitScore(@RequestParam("score") int score) {
        try {
            if (score > 5) {
                discordService.sendNotification("🎮 **GAME BẮT TIM:** Điểm cao: " + score);
            }
            return ResponseEntity.ok("Đã lưu điểm!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi mạng");
        }
    }

    @GetMapping("/catch-game") public String showCatchGamePage() { return "catch-game"; } // Nếu có file này
    @GetMapping("/ticket") public String showTicketPage() { return "ticket"; }
    @GetMapping("/tarot") public String showTarotPage() { return "tarot"; }
    @GetMapping("/kitchen") public String showKitchenPage() { return "kitchen"; }
}