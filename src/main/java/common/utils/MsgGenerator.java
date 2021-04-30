package common.utils;

/**
 * Генерирует сообщения по заданному шаблону
 */
public class MsgGenerator {

    private final static String DELIMITER = "///";

    /**
     * Генерирует сообщение по шаблону
     * ///chatId///msg
     * @param chatId - ID чата
     * @param msg - сообщение
     * @return - строка с генерированным сообщением
     */
    public static String generateMessageToChat(int chatId, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(DELIMITER);
        msgBuilder.append(chatId);
        msgBuilder.append(DELIMITER);
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }

    /**
     * Находит в сообщение сгенерированному по шаблону "///chatId///msg" ID чата
     * @param msg - сообщение
     * @return - ID чата
     */
    public static int getChatIdFromMessageToChat(String msg) {
        return Integer.parseInt(msg.split(DELIMITER, 3)[1]);
    }

    /**
     * Находит в сообщение сгенерированному по шаблону "///chatId///msg" текст сообщения
     * @param msg - сообщение
     * @return - текст сообщения
     */
    public static String getContentFromMessageToChat(String msg) {
        return msg.split(DELIMITER, 3)[2];
    }

    /**
     * Генерирует сообщение по шаблону
     * ///chatId///nickname///msg
     * @param chatId - ID чата
     * @param nickname - никнейм
     * @param msg - сообщение
     * @return - строка с генерированным сообщением
     */
    public static String generateMessageFromNicknameToChat(String nickname, int chatId, String msg) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(DELIMITER);
        msgBuilder.append(chatId);
        msgBuilder.append(DELIMITER);
        msgBuilder.append(nickname);
        msgBuilder.append(DELIMITER);
        msgBuilder.append(msg);
        return msgBuilder.toString();
    }

    /**
     * Находит в сообщение сгенерированному по шаблону "///chatId[.]*" ID чата
     * @param msg - сообщение
     * @return - ID чата
     */
    public static int getChatIdFromMessage(String msg) {
        return Integer.parseInt(msg.split(DELIMITER)[1]);
    }

    /**
     * Находит в сообщение сгенерированному по шаблону "///chatId///nickname///msg" никнейм пользователя
     * @param msg - сообщение
     * @return - ID чата
     */
    public static String getNicknameFromMessageNicknameToChat(String msg) {
        return msg.split(DELIMITER, 4)[2];
    }

    /**
     * Находит в сообщение сгенерированному по шаблону "///chatId///nickname///msg" текст сообщения
     * @param msg - сообщение
     * @return - текст сообщения
     */
    public static String getContentFromMessageNicknameToChat(String msg) {
        return msg.split(DELIMITER, 4)[3];
    }
}
