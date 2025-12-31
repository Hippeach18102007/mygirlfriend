package com.example.ny.Controller;

import com.example.ny.Model.GameMessage;
import com.example.ny.Service.DiscordService;
import com.example.ny.Service.EmailService;
import jakarta.servlet.http.HttpSession; // Nhớ import cái này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class MessageController {

    // --- CẤU HÌNH THÔNG TIN CƠ BẢN ---
    private final String tenChi = "Bích Loan";
    private final String tenEm = "Anh Đức ny của chị";
        private final String loiNhan = "Năm 2025, là 1 năm đầy khó khăn với anh nhất là giai đoạn giữa năm. Lúc đó anh vừa vướng mắc giữa việc học với việc gia đình. Nhưng mà vào gần cuối năm chắc là do đã ăn chè đậu đỏ nên may mắn gặp được em. Mỗi tối về đều có người call với nhắn tin để tâm sự. Từ đó, anh thấy mình may mắn hơn và hạnh phúc. Dân IT nên khô khan trong lời nói. Anh cảm ơn em vì đã yêu anh, thương anh kể cả có những lúc anh sai. Love you 3000! \uD83E\uDEF6\n";
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

    static class PrizeDto {
        private String prize;

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }
    }

    @GetMapping("/vong-quay")
    public String showWheelPage() {
        return "vong-quay";
    }

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

    @GetMapping("/store")
    public String showStorePage() {
        return "store";
    }

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

    @GetMapping("/challenge")
    public String showChallengePage() {
        return "challenge";
    }

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

    @GetMapping("/christmas")
    public String showChristmasPage() {
        return "lixi";
    }

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

    @GetMapping("/game-kho")
    public String showHardGamePage() {
        return "game-kho";
    }

    @GetMapping("/game-2048")
    public String show2048Game() {
        return "game-2048";
    }

    @GetMapping("/game-piano")
    public String showPianoGame() {
        return "game-piano";
    }

    @GetMapping("/game-snake")
    public String showSnakeGame() {
        return "game-snake";
    }

    @GetMapping("/garden")
    public String showGardenPage() {
        return "garden";
    }

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

    @GetMapping("/pharmacy")
    public String showPharmacyPage() {
        return "pharmacy";
    }

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

    @GetMapping("/heart-game")
    public String showHeartGamePage() {
        return "heart-game";
    }

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

    @GetMapping("/catch-game")
    public String showCatchGamePage() {
        return "catch-game";
    } // Nếu có file này

    @GetMapping("/ticket")
    public String showTicketPage() {
        return "ticket";
    }

    @GetMapping("/tarot")
    public String showTarotPage() {
        return "tarot";
    }

    @GetMapping("/kitchen")
    public String showKitchenPage() {
        return "kitchen";
    }

    // --- PHẦN 2: XỬ LÝ GỬI ĐỒ ĂN (POST) ---
    // Cái này giúp nút bấm hoạt động, sửa lỗi 404
    @PostMapping("/api/cook-bento")
    @ResponseBody // Bắt buộc có dòng này để trả về chữ, không phải trả về file HTML
    public ResponseEntity<String> cookBento(
            @RequestParam String dishList,
            @RequestParam String message
    ) {
        // Soạn tin nhắn gửi Discord
        StringBuilder sb = new StringBuilder();
        sb.append("🍱 **TING TING! CƠM VỢ NẤU ĐẾN RỒI!** 🍱\n");
        sb.append("------------------------------------------\n");
        sb.append("👩‍🍳 **Thực đơn:** ").append(dishList).append("\n");
        sb.append("💌 **Lời nhắn:** \"").append(message).append("\"\n");
        sb.append("------------------------------------------\n");
        sb.append("❤️ Chúc chồng yêu ăn ngon miệng!");

        discordService.sendNotification(sb.toString());

        return ResponseEntity.ok("Đã gửi thành công!");
    }

    @GetMapping("/cinema")
    public String showCinemaPage() {
        return "cinema"; // Trả về file cinema.html
    }

    @PostMapping("/api/invite-movie")
    @ResponseBody
    public ResponseEntity<String> inviteMovie(@RequestParam("movieName") String movieName) {
        try {
            String message = "🎬 **LỜI MỜI XEM PHIM!** 🎬\n" +
                    "--------------------------------\n" +
                    "🍿 **Phim:** " + movieName + "\n" +
                    "🥰 **Người mời:** Vợ Yêu\n" +
                    "💬 **Lời nhắn:** \"Phim này hay quá, hôm nào mình cùng xem nha anh!\"\n" +
                    "--------------------------------\n" +
                    "👉 *Anh nhớ sắp xếp thời gian nhé!*";

            discordService.sendNotification(message);
            return ResponseEntity.ok("Đã gửi lời mời xem phim!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống");
        }

    }

    @GetMapping("/star")
    public String showStarPage() {
        return "star"; // Trả về file star.html
    }

    @PostMapping("/api/fold-star")
    @ResponseBody
    public ResponseEntity<String> foldStar(@RequestParam("wish") String wish, @RequestParam("count") int count) {
        try {
            String message = "🌟 **NGÔI SAO HY VỌNG** 🌟\n" +
                    "--------------------------------\n" +
                    "🔢 **Ngôi sao thứ:** " + count + "\n" +
                    "🙏 **Điều ước:** \"" + wish + "\"\n" +
                    "--------------------------------\n" +
                    "👉 *Cố lên! Đủ 1000 ngôi sao là anh về tới nhà rồi!*";

            discordService.sendNotification(message);
            return ResponseEntity.ok("Điều ước đã được gửi tới vũ trụ!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kết nối");
        }
    }

    private String[] board = new String[9];
    private String turn = "X"; // X đi trước

    @GetMapping("/game-online")
    public String showGamePage() {
        return "game-online";
    }

    // Khi người chơi đánh một nước
    @MessageMapping("/move") // Nhận từ /app/move
    @SendTo("/topic/game")   // Gửi ra /topic/game cho cả 2 người
    public GameMessage processMove(GameMessage message) {
        if (message.getType().equals("RESET")) {
            // Reset bàn cờ
            board = new String[9];
            turn = "X";
            return new GameMessage("RESET", -1, "", "Ván mới bắt đầu!");
        }

        // Logic đánh cờ
        if (board[message.getIndex()] == null) {
            board[message.getIndex()] = message.getPlayer();

            // Đổi lượt
            turn = message.getPlayer().equals("X") ? "O" : "X";

            // Kiểm tra thắng thua (Logic đơn giản)
            if (checkWin(message.getPlayer())) {
                discordService.sendNotification("🎮 **KẾT QUẢ:** " + (message.getPlayer().equals("X") ? "Anh Đức" : "Vợ Yêu") + " đã thắng Cờ Caro!");
                return new GameMessage("WIN", message.getIndex(), message.getPlayer(), "Chiến thắng!");
            }

            return message;
        }
        return null; // Ô đã đánh rồi
    }

    private boolean checkWin(String p) {
        // Các trường hợp thắng (0-1-2, 3-4-5, ...)
        int[][] wins = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
        for (int[] w : wins) {
            if (p.equals(board[w[0]]) && p.equals(board[w[1]]) && p.equals(board[w[2]])) return true;
        }
        return false;
    }

    @GetMapping("/adventure")
    public String showAdventurePage() {
        return "adventure"; // Trả về file adventure.html
    }

    // Class để chứa dữ liệu di chuyển
    public static class MoveData {
        public String role; // "FIRE" hoặc "WATER"
        public int x;
        public int y;
        public String action; // "MOVE" hoặc "WIN" hoặc "RESET"
        // Getter, Setter (Bạn tự thêm hoặc dùng public cho nhanh)
    }

    @MessageMapping("/adventure/move")
    @SendTo("/topic/adventure")
    public MoveData syncMove(MoveData data) {
        if ("WIN".equals(data.action)) {
            // Nếu cả 2 cùng thắng
            discordService.sendNotification("🏆 **GAME LỬA & NƯỚC:** Hai bạn đã phá đảo thành công! Đồng tâm hiệp lực quá đỉnh!");
        }
        return data;
    }

    @Controller
    public class ShooterController {

        @Autowired
        private DiscordService discordService;

        // LƯU TRẠNG THÁI GAME TRÊN SERVER
        private static int hpBoy = 100;
        private static int hpGirl = 100;

        @GetMapping("/shooter")
        public String showShooterPage() {
            return "shooter";
        }

        public static class ShooterData {
            public String role; // "BOY" hoặc "GIRL"
            public String action; // "MOVE", "SHOOT", "HIT", "SYNC_HP", "RESET"
            public double y;
            public double bulletY;
            public int hpBoy;  // Gửi về client
            public int hpGirl; // Gửi về client
        }

        @MessageMapping("/shooter/action")
        @SendTo("/topic/shooter")
        public ShooterData handleAction(ShooterData data) {
            ShooterData response = new ShooterData();
            response.role = data.role;
            response.action = data.action;
            response.y = data.y;
            response.bulletY = data.bulletY;

            switch (data.action) {
                case "RESET":
                    hpBoy = 100;
                    hpGirl = 100;
                    response.action = "SYNC_HP";
                    break;

                case "HIT":
                    // Ai bị trúng đạn thì trừ máu người đó
                    if ("BOY".equals(data.role)) { // Role là người BỊ BẮN
                        hpBoy = Math.max(0, hpBoy - 10);
                    } else {
                        hpGirl = Math.max(0, hpGirl - 10);
                    }

                    // Chuyển thành lệnh đồng bộ máu
                    response.action = "SYNC_HP";

                    // Kiểm tra thắng thua
                    if (hpBoy <= 0 || hpGirl <= 0) {
                        String winner = (hpBoy <= 0) ? "Vợ Yêu" : "Anh Đức";
                        discordService.sendNotification("🔫 **ĐẠI CHIẾN TÌNH YÊU:** " + winner + " đã chiến thắng! Người thua chuẩn bị chịu phạt!");
                    }
                    break;

                case "MOVE":
                case "SHOOT":
                    // Giữ nguyên các thông số di chuyển/bắn
                    break;
            }

            // Luôn gửi kèm máu hiện tại để đồng bộ
            response.hpBoy = hpBoy;
            response.hpGirl = hpGirl;

            return response;
        }

        private static int serverMoney = 150;
        private static int serverHealth = 20;

        @GetMapping("/tower")
        public String showGamePagetowerDefensePage() {
            return "tower"; // Trả về file tower.html
        }

        public static class TDAction {
            public String type; // "REQUEST_BUILD", "BUILD_CONFIRMED", "GAME_OVER", "SYNC_STATE", "KILL_ENEMY", "RESET"
            public int x;
            public int y;
            public String towerType;
            public int price;   // Giá tiền tháp (Gửi từ client lên)
            public int money;   // Tiền hiện tại (Gửi về client)
            public int health;  // Máu hiện tại
            public int levelIdx;
        }

        @MessageMapping("/td/action")
        @SendTo("/topic/td")
        public TDAction handleAction(TDAction action) {
            TDAction response = new TDAction();

            switch (action.type) {
                case "RESET": // Chơi lại từ đầu
                    serverMoney = 150;
                    serverHealth = 20;
                    response.type = "SYNC_STATE";
                    response.money = serverMoney;
                    response.health = serverHealth;
                    response.levelIdx = action.levelIdx; // Báo chuyển map
                    break;

                case "REQUEST_BUILD": // Người chơi xin xây tháp
                    if (serverMoney >= action.price) {
                        serverMoney -= action.price; // Server trừ tiền

                        // Trả về lệnh xác nhận xây
                        response.type = "BUILD_CONFIRMED";
                        response.x = action.x;
                        response.y = action.y;
                        response.towerType = action.towerType;
                        response.money = serverMoney; // Gửi số tiền chuẩn về
                    } else {
                        // Không đủ tiền -> Gửi gói tin rỗng hoặc loại bỏ (Client tự hiểu)
                        return null;
                    }
                    break;

                case "KILL_ENEMY": // Giết quái được tiền
                    serverMoney += 10;
                    response.type = "SYNC_STATE";
                    response.money = serverMoney;
                    response.health = serverHealth;
                    break;

                case "ENEMY_REACH_GOAL": // Quái chạm đích -> Trừ máu
                    serverHealth = Math.max(0, serverHealth - 1);
                    response.type = "SYNC_STATE";
                    response.money = serverMoney;
                    response.health = serverHealth;

                    if (serverHealth <= 0) {
                        response.type = "GAME_OVER";
                        discordService.sendNotification("🏰 **BẢO VỆ TRÁI TIM:** Thất thủ rồi! Game Over!");
                    }
                    break;

                case "START_WAVE":
                    response.type = "START_WAVE";
                    break;

                default:
                    return action;
            }
            return response;
        }
    }
    private final String[] REWARDS = {
            // --- Hạng S: Quà xịn (Tỷ lệ thấp) ---
            "💰 Ting ting 100k (Lộc rơi trúng đầu!)",
            "💄 1 Thỏi son (Em chọn, anh trả tiền - Giới hạn 300k)",
            "👗 1 Cái váy mới (Anh dẫn đi mua)",
            "👑 Phiếu 'Nữ Hoàng' (Anh làm hết việc nhà 1 ngày)",

            // --- Hạng A: Ăn uống & Chơi bời ---
            "🧋 1 Ly Trà Sữa Full Topping (Size L)",
            "🍗 1 Chầu Gà Rán (Anh mời)",
            "🍕 1 Cái Pizza (Anh trả tiền)",
            "🎬 1 Vé xem phim (Em chọn phim)",
            "🍢 1 Chầu Xiên bẩn / Nem chua rán",
            "🍦 1 Cây kem ốc quế",

            // --- Hạng B: Sai vặt & Phục vụ ---
            "💆‍♀️ Massage cổ vai gáy 30 phút",
            "💆‍♂️ Gội đầu cho vợ",
            "🦶 Bóp chân cho vợ 15 phút",
            "💇‍♂️ Sấy tóc cho vợ",
            "🥣 Rửa bát hôm nay (Không được kêu ca)",
            "🧹 Quét nhà + Lau nhà",
            "👕 Gấp quần áo cho vợ",
            "🏍️ Làm tài xế riêng chở đi lượn phố 1 tiếng",

            // --- Hạng C: Quyền lực ---
            "🤫 Phiếu 'Anh Im Lặng' (Anh không được cãi 1 lần)",
            "📱 Được kiểm tra điện thoại anh 5 phút",
            "📷 Anh phải để Avatar đôi theo ý em 3 ngày",
            "🎤 Anh phải hát 1 bài tặng em",
            "🥺 Phiếu 'Tha Thứ' (Xóa 1 lỗi lầm cũ của anh)",

            // --- Hạng D: Troll & An ủi (Cho vui) ---
            "💧 1 Cốc nước lọc (Tốt cho sức khỏe)",
            "🤝 1 Cái bắt tay nồng ấm",
            "😘 1 Cái thơm vào má",
            "🤡 Chúc bạn may mắn lần sau!",
            "👀 Anh sẽ nhìn em đắm đuối 1 phút",
            "💪 Anh hít đất 20 cái cho em xem"
    };

    @GetMapping("/gacha")
    public String showGachaPage() {
        return "gacha";
    }

    @PostMapping("/api/gacha-pull")
    @ResponseBody
    public ResponseEntity<String> pullGacha() {
        Random rand = new Random();
        String reward = REWARDS[rand.nextInt(REWARDS.length)];

        StringBuilder sb = new StringBuilder();
        sb.append("🎰 **KẾT QUẢ QUAY SỐ NHÂN PHẨM** 🎰\n");
        sb.append("------------------------------------------\n");
        sb.append("Chúc mừng **Vợ Yêu** đã quay vào ô:\n\n");
        sb.append("# 🎉 ").append(reward).append(" 🎉\n\n");
        sb.append("------------------------------------------\n");
        sb.append("⚠️ _Anh Đức nhớ thực hiện ngay nhé!_");

        discordService.sendNotification(sb.toString());

        return ResponseEntity.ok(reward);
    }
    @GetMapping("/cycle")
    public String showCalendarPage() {
        return "cycle"; // Trả về file calendar.html
    }

    // Class chứa dữ liệu thô để JS xử lý
    public static class CycleEvent {
        public String startDate;      // Ngày bắt đầu dâu (yyyy-MM-dd)
        public String ovulationDate;  // Ngày rụng trứng (yyyy-MM-dd)

        public CycleEvent(String startDate, String ovulationDate) {
            this.startDate = startDate;
            this.ovulationDate = ovulationDate;
        }
    }

    @GetMapping("/api/cycle-data")
    @ResponseBody
    public ResponseEntity<List<CycleEvent>> getCycleData(
            @RequestParam String startDate // Nhập: 2025-12-20
    ) {
        List<CycleEvent> events = new ArrayList<>();
        LocalDate currentPeriod = LocalDate.parse(startDate);

        // Tính toán cho 15 chu kỳ tới (dư ra một chút để phủ kín năm)
        for (int i = 0; i < 15; i++) {
            LocalDate ovulation = currentPeriod.plusDays(14); // Rụng trứng (giả định)

            events.add(new CycleEvent(
                    currentPeriod.toString(),
                    ovulation.toString()
            ));

            // Chu kỳ tiếp theo (+28 ngày)
            currentPeriod = currentPeriod.plusDays(28);
        }

        return ResponseEntity.ok(events);
    }
    @GetMapping("/lixi")
    public String showLiXiPage() {
        return "lixi";
    }

    // ... (Giữ nguyên class Prize và List prizes như cũ) ...
    static class Prize {
        String name;
        int value;
        double weight;

        public Prize(String name, int value, double weight) {
            this.name = name;
            this.value = value;
            this.weight = weight;
        }
    }

    // Ví dụ danh sách giải thưởng (dùng cái mới nhất bạn đã chỉnh)
    private final List<Prize> prizes = new ArrayList<>(Arrays.asList(
            new Prize("10.000 VNĐ", 10000, 10.0),
            new Prize("20.000 VNĐ", 20000, 20.0),
            new Prize("50.000 VNĐ", 50000, 40.0),
            new Prize("100.000 VNĐ", 100000, 20.0),
            new Prize("200.000 VNĐ", 200000, 7.0),
            new Prize("500.000 VNĐ", 500000, 3.0)
    ));

    @PostMapping("/api/boc-lixi")
    @ResponseBody
    public ResponseEntity<String> getLuckyMoney() {
        // 1. Logic chọn giải thưởng (Giữ nguyên)
        double totalWeight = 0.0;
        for (Prize p : prizes) totalWeight += p.weight;

        double random = new Random().nextDouble() * totalWeight;
        Prize selectedPrize = null;

        for (Prize p : prizes) {
            random -= p.weight;
            if (random <= 0.0) {
                selectedPrize = p;
                break;
            }
        }
        if (selectedPrize == null) selectedPrize = prizes.get(0);

        // 2. GỬI THÔNG BÁO VỀ DISCORD NGAY LẬP TỨC
        // Lấy giờ hiện tại cho uy tín
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));

        String discordMessage = String.format(
                "🚨 **CÓ BIẾN CÓ BIẾN!** 🚨\\n" +
                        "--------------------------\\n" +
                        "👤 **Người chơi:** Chị Yêu\\n" +
                        "💰 **Vừa rút được:** **%s**\\n" +
                        "⏰ **Thời gian:** %s\\n" +
                        "💸 **Chuẩn bị tiền đi em ơi!** 😭",
                selectedPrize.name, time
        );

        // Chạy bất đồng bộ (Thread mới) để chị không bị lag khi chờ Discord phản hồi
        Prize finalSelectedPrize = selectedPrize; // Biến final để dùng trong lambda
        new Thread(() -> {
            discordService.sendNotification(discordMessage);
        }).start();

        // 3. Trả kết quả về cho giao diện web
        return ResponseEntity.ok(selectedPrize.name);
    }
}