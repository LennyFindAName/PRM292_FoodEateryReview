package prm392.project.model;

public class MessageModel {
    private String content;
    private String senderName;
    private boolean isBot;
    private long timestamp;

    public MessageModel(String content, String senderName, boolean isBot, long timestamp) {
        this.content = content;
        this.senderName = senderName;
        this.isBot = isBot;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean isBot() {
        return isBot;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 