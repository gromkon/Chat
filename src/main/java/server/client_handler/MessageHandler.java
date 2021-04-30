package server.client_handler;

import common.data.GeneralChatData;
import common.info.ChatInfo;
import common.status.AddUserToChatStatus;
import common.status.ChatCreateStatus;
import common.status.RegStatus;
import common.system_messages.Messages;
import common.utils.MsgGenerator;
import server.status.OnlineStatus;
import server.utils.DBService;
import server.server_handler.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class MessageHandler extends Thread {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ClientHandler ch;

    private String nickname;
    private int id;

    public MessageHandler(Server server, Socket socket, DataInputStream in, DataOutputStream out, ClientHandler ch) {
        this.server = server;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.ch = ch;
    }

    public String getNickname() {
        return nickname;
    }

    public int getUserId() {
        return id;
    }

    /**
     * Запускает обработчика сообщений
     * Сначала происходит авторизация и/или регистрация, а после этого обрабатываются входящие сообщения
     */
    public void run() {
        try {
            authAndReg();
            handlingInputMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                server.deleteUser(ch);
                server.broadcastMessage(this.nickname, GeneralChatData.getGeneralChatId(), Messages.USER_DISCONNECTED.msg);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Осуществляет авторизацию и регистрацию пользователя
     */
    private void authAndReg() throws IOException {
        while (true) {
            if (socket.isClosed()) {
                break;
            }
            try {
                String msg = in.readUTF();
                if (msg.startsWith(Messages.AUTH_SERVER_COMMAND_MESSAGE.msg)) {
                    if (tryAuth(msg)) {
                        DBService.setStatus(nickname, OnlineStatus.getONLINE());
                        ch.setChatIds(DBService.getUserChatList(String.valueOf(this.id)));
                        break;
                    }
                } else if (msg.startsWith(Messages.REG_COMMAND_MESSAGE.msg)) {
                    tryReg(msg);
                }
            } catch (SocketException se) {
                break;
            }
        }
    }

    /**
     * Попытка подключить пользователя к чату.
     * Если введена не правильная пара (логин, пароль), подключение не осуществляется, и пользователю отправляется соответственное сообщение.
     * Если введена правильная пара (логин, пароль) и если пользователь онлайн, подключение не осуществляется, и пользователю отправляется соответственное сообщение.
     * Если введена правильна пара (логин, пароль) и если пользователь не онлайн, осуществляется подключение, пользоватлю отправляется соответсвенное сообщение,
     * а так же в общий чат отправляется сообщение о том, что данный ползователь вошел в чат.
     * @param msg - сообщение с информацией о логине и пароле
     * @return - результат подключения
     */
    private boolean tryAuth(String msg) {
        String login = msg.split(" ")[1];
        String pass = msg.split(" ")[2];
        String nickname = DBService.getNicknameByLoginAndPass(login, pass);
        if (nickname != null) {
            if (server.checkUserOnline(nickname)) {
                sendMsgNotAuth(Messages.USER_ALREADY_ONLINE.msg);
                return false;
            } else {
                this.nickname = nickname;
                this.id = DBService.getIdByNickname(this.nickname);
                server.broadcastMessage(this.nickname, GeneralChatData.getGeneralChatId(), Messages.USER_CONNECTED.msg);
                ArrayList<ChatInfo> chats = DBService.getUserChatList(String.valueOf(id));
                sendMsgAuth(Messages.AUTH_OK.msg + chats);
                server.addUser(ch);
                return true;
            }
        } else {
            sendMsgNotAuth(Messages.INCORRECT_LOGIN_OR_PASSWORD.msg);
            return false;
        }
    }

    /**
     * Попытка зарегестрировать пользователя
     * Если никнейм или логин заняты, то пользователю отправляется соответсвенное сообщение
     * Если никнейм и логин не заняты, то осуществляется регистрация и пользователю отправляется соответственное сообщение
     * @param msg - сообщение с информацией о никнейме, логине, пароле
     * @return - результат регистрации
     */
    private boolean tryReg(String msg) {
        String nickname = msg.split(" ")[1];
        String login = msg.split(" ")[2];
        String pass = msg.split(" ")[3];
        RegStatus status = DBService.reg(nickname, login, pass);
        switch (status) {
            case LOGIN_USED:
            case NICKNAME_USED:
            case ERROR:
                sendMsgNotAuth(Messages.NOT_REGISTRATED_COMMAND_MESSAGE.msg + status.answer);
                return false;
            case OK:
                this.id = DBService.getIdByNickname(nickname);
                DBService.addUserToChat(GeneralChatData.getGeneralChatIdString(), String.valueOf(this.id));
                sendMsgAuth(Messages.REGISTRATED_COMMAND_MESSAGE.msg + status.answer);
                return true;
        }
        return false;
    }

    /**
     * Осуществляет прием и обработку сообщений от клиента
     */
    private void handlingInputMessages() {
        while (true) {
            try {
                String msg = in.readUTF();
                if (msg.equals(Messages.STOP_CLIENT_COMMAND_MESSAGE.msg)) {
                    DBService.setStatus(nickname, OnlineStatus.getOFFLINE());
                    DBService.addSystemMessage(id, msg);
                    break;
                } else {
                    int chatId = MsgGenerator.getChatIdFromMessageToChat(msg);
                    String content = MsgGenerator.getContentFromMessageToChat(msg);
                    DBService.addChatMessage(chatId, id, content);
                    if (content.startsWith(Messages.CREATE_CHAT.msg)) {
                        tryCreateNewChat(chatId, content);
                    } else if (content.equals(Messages.HELP.msg)) {
                        sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, Messages.HELP_INFO.msg));
                    } else if (content.startsWith(Messages.ADD_USER_TO_CHAT.msg)) {
                        tryAddUserToChat(chatId, content);
                    } else {
                        server.broadcastMessage(nickname, chatId, content);
                    }
                }
            } catch (IOException e) {
                break;
            }

        }
    }

    /**
     * Попытка добавления пользователя в чат
     * @param chatId - ID чата из которого делается запрос
     * @param msg - сообщение с никнеймом пользователя
     */
    private void tryAddUserToChat(int chatId, String msg) {
        String nickname = msg.substring(Messages.ADD_USER_TO_CHAT.msg.length() + 1);
        int userId = DBService.getIdByNickname(nickname);
        switch (DBService.addUserToChat(String.valueOf(chatId), String.valueOf(userId))) {
            case ERROR:
                sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, AddUserToChatStatus.ERROR.answer));
                break;
            case ALREADY_IN_CHAT:
                sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, AddUserToChatStatus.ALREADY_IN_CHAT.answer));
                break;
            case NO_USER:
                sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, AddUserToChatStatus.NO_USER.answer));
                break;
            case OK:
                server.broadcastMessage(chatId, Messages.USER_COME_IN_CHAT.msg + nickname);
                break;
        }
    }

    /**
     * Попытка создания нового чата
     * @param chatId - ID чата из которого делается запрос
     * @param msg - сообщение с названием нового чата
     */
    private void tryCreateNewChat(int chatId, String msg) {
        String chatName = msg.substring(Messages.CREATE_CHAT.msg.length() + 1);
        if (isChatNameValid(chatName)) {
            switch (DBService.newChat(chatName, String.valueOf(this.id))) {
                case WRONG_NAME:
                    sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, ChatCreateStatus.WRONG_NAME.answer));
                    break;
                case ERROR:
                    sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, ChatCreateStatus.ERROR.answer));
                    break;
                case OK:
                    ch.addToChat(DBService.getChatIdByChatNameAndUserId(chatName, String.valueOf(id)));
                    sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, ChatCreateStatus.OK.answer));
                    sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, Messages.BROADCAST_CHATS_LIST.msg + DBService.getUserChatList(String.valueOf(this.id))));
                    break;
            }
        } else {
            sendMsgAuth(MsgGenerator.generateMessageToChat(chatId, Messages.BAD_CHAT_NAME.msg));
        }
    }

    /**
     * Проверяет чтобы в названии чата была только кириллица, латиница и цифры
     * @param chatName - название чата
     * @return - правильность написания названия чата
     */
    private boolean isChatNameValid(String chatName) {
        return chatName.matches("^[a-zA-Zа-яА-Я0-9]+");
    }

    /**
     * Отправляет сообщение не авторизированному пользователю
     * @param msg - сообщение
     */
    private void sendMsgNotAuth(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправляет сообщение авторизированному пользователю
     * @param msg - сообщение
     */
    public void sendMsgAuth(String msg) {
        try {
            out.writeUTF(msg);
            DBService.addSystemMessage(id, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
