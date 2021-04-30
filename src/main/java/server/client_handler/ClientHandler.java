package server.client_handler;

import common.data.GeneralChatData;
import common.info.ChatInfo;
import common.system_messages.Messages;
import server.server_handler.Server;
import common.utils.MsgGenerator;
import server.utils.DBService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

    private Server server;
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private ArrayList<String> chatIds;
    private MessageHandler messageHandler;

    public ClientHandler(Server server, Socket socket) {
        initialize(server, socket);

        messageHandler = new MessageHandler(this.server, this.socket, in, out, this);
        messageHandler.start();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * Иницилазирует обработчка клиента
     * @param server - сервер, к которому подключен клиент
     * @param socket - сокет, по которому подключен клиент
     */
    private void initialize(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.chatIds = new ArrayList<>();
            chatIds.add(GeneralChatData.getGeneralChatIdString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет находится ли пользователя в конкретном чате
     * @param id - ID чата
     * @return - находится ли пользователь в конкретном чате
     */
    public boolean inChat(int id) {
        String sId = String.valueOf(id);
        return chatIds.contains(sId);
    }

    /**
     * Отправляет пользователю сообщение от другого пользователя в чат
     * @param msg - сообщение
     */
    public void sendMsg(String nickname, int chatId, String msg) {
        try {
            out.writeUTF(MsgGenerator.generateMessageFromNicknameToChat(nickname, chatId, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет пользователю сообщение в чат
     * @param msg - сообщение
     */
    public void sendMsg(int chatId, String msg) {
        try {
            out.writeUTF(MsgGenerator.generateMessageToChat(chatId, msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Добавляет пользователя в чат
     * @param chatId - ID чата
     */
    public void addToChat(String chatId) {
        chatIds.add(chatId);
        messageHandler.sendMsgAuth(
                MsgGenerator.generateMessageToChat(
                        Integer.parseInt(chatId),
                        Messages.BROADCAST_CHATS_LIST.msg + DBService.getUserChatList(String.valueOf(messageHandler.getUserId()))
                )
        );

    }

    /**
     * Заменяет ID чатов на новые
     * @param chatIds - объект, в котором содержится информация о чатах
     */
    public void setChatIds(ArrayList<ChatInfo> chatIds) {
        ArrayList<String> ci = new ArrayList<>();
        for (ChatInfo chatInfo: chatIds) {
            ci.add(chatInfo.getChatId());
        }
        this.chatIds = ci;
    }

    /**
     * Удаляет пользователя из чата
     * @param chatId - ID чата
     */
    public void deleteFromChat(int chatId) {
        chatIds.remove((Object) chatId);
    }
}
