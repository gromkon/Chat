package common.info;

/**
 * Содержит пару (имя чата, ID чата)
 */
public class ChatInfo {

    private String chatName;
    private String chatId;

    public ChatInfo(String chatName, String chatId) {
        this.chatName = chatName;
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getChatId() {
        return chatId;
    }

    @Override
    public String toString() {
        return chatName + "///" + chatId;
    }
}
