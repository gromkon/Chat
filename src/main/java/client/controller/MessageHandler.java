package client.controller;

import common.status.AddUserToChatStatus;
import common.status.ChatCreateStatus;
import common.system_messages.Messages;
import common.utils.MsgGenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MessageHandler extends Thread {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Controller controller;

    public MessageHandler(Socket socket, DataInputStream in, DataOutputStream out, Controller controller) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.controller = controller;
    }

    /**
     * Запускает поток. Сначала производит авторизацию и/или регистрацию, а после осуществляет обработку входящих сообщений
     */
    public void run() {
        try {
            authAndReg();
            handlingInputMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller.textArea.clear();
            controller.setAuth(false);
        }

    }

    /**
     * Осуществляет авторизацию и регистрацию пользователя
     */
    private void authAndReg() throws IOException {
        while (true) {
            String msg = in.readUTF();
            if (msg.startsWith(Messages.AUTH_OK.msg)) {
                controller.setChatList(parseMessageToChatsList(msg, Messages.AUTH_OK.msg));
                controller.setAuth(true);
                controller.textArea.clear();
                break;
            } else if (msg.startsWith(Messages.REGISTRATED_COMMAND_MESSAGE.msg)) {
                String msgText = msg.split(" ", 2)[1];
                controller.textArea.appendText(msgText);
                controller.setAuth(false);
                controller.activeRegPanel();
            } else if (msg.startsWith(Messages.NOT_REGISTRATED_COMMAND_MESSAGE.msg)) {
                String msgText = msg.split(" ", 2)[1];
                controller.textArea.appendText(msgText);
                controller.setAuth(false);
                controller.activeRegPanel();
            } else {
                controller.setAuth(false);
                controller.textArea.appendText(msg + "\n");
            }
        }
    }

    /**
     * Парсит сообщение и возвращает список чатов
     */
    private ArrayList<Chat> parseMessageToChatsList(String msg, String command) {
        ArrayList<Chat> chatsInfo = new ArrayList<>();
        String info = msg.substring(command.length() + 1, msg.length() - 1);
        String[] chatInfo = info.split(", ");
        for (String ci: chatInfo) {
            String chatName = ci.substring(0, ci.lastIndexOf("///"));
            String chatId = ci.substring(ci.lastIndexOf("///") + 3);
            chatsInfo.add(new Chat(chatName, chatId));
        }
        return chatsInfo;
    }

    /**
     * Осуществляет прием и обработку сообщений от сервера
     */
    private void handlingInputMessages() throws IOException {
        while (true) {
            String msg = in.readUTF();
            if (msg.equals(Messages.STOP_SERVER_COMMAND_MESSAGE.msg)) {
                break;
            } else {
                int chatId = MsgGenerator.getChatIdFromMessage(msg);
                String msgNoChatId = msg.substring(3 + String.valueOf(chatId).length() + 3);
                if (msgNoChatId.startsWith(Messages.BROADCAST_CHATS_LIST.msg)) {
                    controller.setChatList(parseMessageToChatsList(msgNoChatId, Messages.BROADCAST_CHATS_LIST.msg));
                } else if (msgNoChatId.equals(ChatCreateStatus.OK.answer) || msgNoChatId.equals(ChatCreateStatus.WRONG_NAME.answer) || msgNoChatId.equals(ChatCreateStatus.ERROR.answer) || msgNoChatId.equals(Messages.BAD_CHAT_NAME.msg)
                        || msgNoChatId.startsWith(Messages.USER_COME_IN_CHAT.msg) || msgNoChatId.equals(AddUserToChatStatus.NO_USER.answer) || msgNoChatId.equals(AddUserToChatStatus.ALREADY_IN_CHAT.answer) || msgNoChatId.equals(AddUserToChatStatus.ERROR.answer) || msgNoChatId.equals(AddUserToChatStatus.OK.answer) ) {
                    controller.addMessage(String.valueOf(chatId), msgNoChatId + "\n");
                } else if (msgNoChatId.equals(Messages.HELP_INFO.msg)) {
                    controller.addMessage(String.valueOf(chatId), msgNoChatId + "\n");
                } else {
                    String nickname = MsgGenerator.getNicknameFromMessageNicknameToChat(msg);
                    String content = MsgGenerator.getContentFromMessageNicknameToChat(msg);
                    controller.addMessage(String.valueOf(chatId), nickname + ": " + content + "\n");
                }
            }
        }
    }

}
