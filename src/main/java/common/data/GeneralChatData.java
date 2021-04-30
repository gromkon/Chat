package common.data;

/**
 * Содержит информацию об общем чате
 */
public class GeneralChatData {

    private final static int GENERAL_CHAT_ID = 1;
    private final static String GENERAL_CHAT_NAME = "Общий";

    public static int getGeneralChatId() {
        return GENERAL_CHAT_ID;
    }

    public static String getGeneralChatIdString() {
        return String.valueOf(GENERAL_CHAT_ID);
    }

    public static String getGeneralChatName() {
        return GENERAL_CHAT_NAME;
    }
}
