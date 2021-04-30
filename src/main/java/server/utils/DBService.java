package server.utils;

import common.info.ChatInfo;
import common.status.AddUserToChatStatus;
import common.status.ChatCreateStatus;
import common.status.MsgStatus;
import common.status.RegStatus;
import common.utils.TimeNow;

import java.sql.*;
import java.util.ArrayList;

public class DBService {

    private static Connection connection;
    private static Statement stmt;

    /**
     * Осуществляет подключение к БД
     */
    public static void connect() {
        /*
            serverName, serverPort, databaseName, user, password init here
         */
        String connectionString = "jdbc:sqlserver://" +
                serverName + ":" + serverPort +
                ";databaseName=" + databaseName +
                ";user=" + user +
                ";password=" + password;
        try {
            connection = DriverManager.getConnection(connectionString);
            stmt = connection.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Осуществляет отключение от БД
     */
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверяет существование комбинации (логин, пароль)
     * @param login - логин пользователя
     * @param password - хэш пароля пользователя
     * @return - правильна ли комбинация (логин, пароль)
     */
    public static boolean isUser(String login, String password) {
        String sql = String.format("SELECT nickname FROM [user] WHERE login = '%s' and password = '%s'", login, password);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Меняет статус пользователя
     * @param nickname - логин пользователя
     * @return - true - такой пользователь существует и его статус изменен, false - такого пользователя не существует
     */
    public static boolean setStatus(String nickname, int status) {
        String sql = String.format("UPDATE [user]\n" +
                " SET status = '%s'\n" +
                " WHERE nickname = '%s'", status, nickname);
        try {
            return stmt.executeUpdate(sql) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Проверяет онлайн ли на данный момент пользователь
     * @param nickname - никнейм пользователя
     * @return - онлайн ли пользователь на данный момент
     */
    public static boolean isUserOnline(String nickname) {
        String sql = String.format("SELECT status FROM [user] WHERE nickname = '%s'", nickname);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("status").equals("1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Проверяет, зарегестрирован ли пользователь с данным логином
     * @param login - логин пользователя
     * @return - существует ли пользователь с таким логином
     */
    public static boolean isRegLogin(String login) {
        String sql = String.format("SELECT * FROM [user] WHERE login = '%s'", login);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Проверяет, зарегестрирован ли пользователь с данным никнеймом
     * @param nickname - никнейм пользователя
     * @return - существует ли пользователь с таким никнеймом
     */
    public static boolean isRegNickname(String nickname) {
        String sql = String.format("SELECT * FROM [user] WHERE nickname = '%s'", nickname);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Проверяет, зарегестрирован ли пользователь с данным ID
     * @param userId - ID пользователя
     * @return - существует ли пользователь с таким ID
     */
    public static boolean isRegId(String userId) {
        String sql = String.format("SELECT * FROM [user] WHERE userId = '%s'", userId);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Возвращает никнейм пользователя связанный с парой (логин, пароль)
     * @param login - логин пользователя
     * @param password - пароль пользователя
     * @return - никнейм пользователя, если пользователя не сущесвует, возвращает null
     */
    public static String getNicknameByLoginAndPass(String login, String password) {
        String sql = String.format("SELECT nickname FROM [user] WHERE login = '%s' and password = '%s'", login, password);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("nickname");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Возвращает ID пользователя связанный с никнеймом
     * @param nickname - никнейм пользователя
     * @return - ID пользователя, если пользователя не существует, возвращает 0
     */
    public static int getIdByNickname(String nickname) {
        String sql = String.format("SELECT userId FROM [user] WHERE nickname = '%s'", nickname);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return Integer.parseInt(rs.getString("userId"));
            } else {
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Регистрирует пользователя
     * @param nickname - никнейм пользователя
     * @param login - логин пользователя
     * @param password - хэш пароля пользователя
     * @return - возвращает статус регистрации
     */
    public static RegStatus reg(String nickname, String login, String password) {
        if (isRegLogin(login)) {
            return RegStatus.LOGIN_USED;
        }
        if (isRegNickname(nickname)) {
            return RegStatus.NICKNAME_USED;
        }
        String sql = String.format("insert into [user] \n" +
                "(nickname, login, password)\n" +
                "values \n" +
                "('%s', '%s', '%s');\n",
                nickname, login, password);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                return RegStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return RegStatus.ERROR;
    }

    /**
     * Добавляет сообщение типа 'chat' в БД
     * @param chatId - ID чата
     * @param userId - ID пользователя
     * @param content - текст сообщения
     * @return - отправлено ли сообщение
     */
    public static MsgStatus addChatMessage(int chatId, int userId, String content) {
        String date = TimeNow.getDate();
        String sql = String.format("insert into [message] \n" +
                "(chatId, userId, content, date, type)\n" +
                "values \n" +
                "('%s', '%s', '%s', '%s', 'chat');\n",
                chatId, userId, content, date);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                return MsgStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MsgStatus.ERROR;
    }

    /**
     * Добавляет сообщение типа system в БД
     * @param userId - ID пользователя
     * @param content - текст сообщения
     * @return - отправлено ли сообщение
     */
    public static MsgStatus addSystemMessage(int userId, String content) {
        String date = TimeNow.getDate();
        String sql = String.format("insert into [message] \n" +
                        "(userId, content, date, type)\n" +
                        "values \n" +
                        "('%s', '%s', '%s', 'system');\n",
                        userId, content, date);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                return MsgStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return MsgStatus.ERROR;
    }

    /**
     * Создает чат c администратором
     * @param chatName - имя чата
     * @param userId - ID администратора чата
     * @return - статус создания чата
     */
    public static ChatCreateStatus newChat(String chatName, String userId) {
        if (getChatIdByChatNameAndUserId(chatName, userId) != null) {
            return ChatCreateStatus.WRONG_NAME;
        }
        String sql = String.format("insert into [chat] \n" +
                "(name, userId)\n" +
                "values \n" +
                "('%s', '%s');\n",
                chatName, userId);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                addUserToChat(getChatIdByChatNameAndUserId(chatName, userId), userId);
                return ChatCreateStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ChatCreateStatus.ERROR;
    }

    /**
     * Находит ID чата по названию чата и ID создателя чата
     * @param chatName - название чата
     * @param userId - ID создателя чата
     * @return - если такого чата нет возвращает null
     * если такой чат есть возвращает ID чата
     */
    public static String getChatIdByChatNameAndUserId(String chatName, String userId) {
        String sql = String.format("SELECT * FROM [chat] WHERE name = '%s' and userId = '%s'", chatName, userId);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("chatId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Проверяет, находится ли пользователь в чате
     * @param chatId - ID пользователя
     * @param userId - ID чата
     * @return - находится ли пользователь в чате
     */
    public static boolean checkUserInChat(String chatId, String userId) {
        String sql = String.format("SELECT * FROM [chat_user_int] WHERE chatId = '%s' and userId = '%s'", chatId, userId);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Добавляет пользователя в чат
     * @param chatId - название чата
     * @param userId - ID пользователя
     * @return - удалось ли добавить пользователя в чат
     */
    public static AddUserToChatStatus addUserToChat(String chatId, String userId) {
        if (!isRegId(userId)) {
            return AddUserToChatStatus.NO_USER;
        }
        if (checkUserInChat(chatId, userId)) {
            return AddUserToChatStatus.ALREADY_IN_CHAT;
        }
        String sql = String.format("insert into [chat_user_int] \n" +
                "(chatId, userId)\n" +
                "values \n" +
                "('%s', '%s');\n",
                chatId, userId);
        try {
            if (stmt.executeUpdate(sql) != 0) {
                return AddUserToChatStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return AddUserToChatStatus.ERROR;
    }

    /**
     * Возвращает список чатов, в которых состоит пользователь
     * @param userId - ID пользователя
     * @return - ArrayList<ChatInfo> в котором содержится информация о всех чатах, в которых состоит пользователь
     */
    public static ArrayList<ChatInfo> getUserChatList(String userId) {
        ArrayList<String> chatIds = new ArrayList<>();
        ArrayList<ChatInfo> chatList = new ArrayList<>();
        String sql = String.format("SELECT * FROM [chat_user_int] WHERE userId = '%s'", userId);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                chatIds.add(rs.getString("chatId"));
            }
            for (String chatId: chatIds) {
                String sqlChatName = String.format("SELECT * FROM [chat] WHERE chatId = '%s'", chatId);
                ResultSet rsChatName = stmt.executeQuery(sqlChatName);
                rsChatName.next();
                String chatName = rsChatName.getString("name");
                chatList.add(new ChatInfo(chatName, chatId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatList;
    }

}
