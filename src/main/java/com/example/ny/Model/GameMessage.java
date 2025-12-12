package com.example.ny.Model;

public class GameMessage {
    private String type; // "MOVE", "RESET", "CHAT"
    private int index;   // Vị trí đánh (0-8)
    private String player; // "X" hoặc "O"
    private String content; // Nội dung chat hoặc thông báo

    // Constructor, Getters, Setters
    public GameMessage() {}
    public GameMessage(String type, int index, String player, String content) {
        this.type = type;
        this.index = index;
        this.player = player;
        this.content = content;
    }

    // Bạn tự generate Getters/Setters nhé, hoặc dùng Lombok @Data
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    public String getPlayer() { return player; }
    public void setPlayer(String player) { this.player = player; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}