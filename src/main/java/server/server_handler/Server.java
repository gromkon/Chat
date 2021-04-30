package server.server_handler;

import common.system_messages.Messages;
import server.utils.DBService;
import server.client_handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private final static String SERVER_ONLINE_OK = "Server online!";
    private final static String SERVER_ONLINE_ERROR = "Error when starting the server";

    private ServerSocket server;

    private ArrayList<ClientHandler> onlineUsers;

    public Server(int port) {
        onlineUsers = new ArrayList<>();
        if (launchServer(port)) {
            System.out.println(SERVER_ONLINE_OK);
        } else {
            System.out.println(SERVER_ONLINE_ERROR);
            return;
        }
        usersConnection();
    }

    /**
     * Запускает сервер
     * @param port - порт на котором запускается сервер
     * @return - запущен ли сервер
     */
    private boolean launchServer(int port) {
        try {
            DBService.connect();
            server = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Осуществляет подключение пользователей к серверу
     */
    private void usersConnection() {
        try {
            while (true) {
                Socket socket = server.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBService.disconnect();
        }
    }

    /**
     * Отправляет сообщение пользователя в конкретный чат
     * @param nickname - никнейм пользователя
     * @param chatId - ID чата
     * @param msg - текст сообщения
     */
    public void broadcastMessage(String nickname, int chatId, String msg) {
        for (ClientHandler user: onlineUsers) {
            if (user.inChat(chatId)) {
                user.sendMsg(nickname, chatId, msg);
            }
        }
    }

    /**
     * Отправляет сообщение  в конкретный чат
     * @param chatId - ID чата
     * @param msg - текст сообщения
     */
    public void broadcastMessage(int chatId, String msg) {
        if (msg.startsWith(Messages.USER_COME_IN_CHAT.msg)) {
            String nickname = msg.substring(msg.lastIndexOf(" ") + 1);
            System.out.println(nickname);
            for (ClientHandler user: onlineUsers) {
                if (user.getMessageHandler().getNickname().equals(nickname)) {
                    user.addToChat(String.valueOf(chatId));
                }
            }
        }
        DBService.addSystemMessage(chatId, msg);
        for (ClientHandler user: onlineUsers) {
            if (user.inChat(chatId)) {
                user.sendMsg(chatId, msg);
            }
        }
    }

    /**
     * Проверяет онлайн статус пользователя
     * @param nickname - никнейм пользователя
     * @return - онлайн статус пользователя
     */
    public boolean checkUserOnline(String nickname) {
        return DBService.isUserOnline(nickname);
    }

    /**
     * Добавляет пользователя в онлайн список пользователей
     * @param ch - пользователь
     */
    public void addUser(ClientHandler ch) {
        onlineUsers.add(ch);
    }

    /**
     * Удаляет пользователя из списка онлайн пользователей
     * @param ch - пользователь
     */
    public void deleteUser(ClientHandler ch) {
        onlineUsers.remove(ch);
    }
}
