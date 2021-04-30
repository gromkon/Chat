package common.system_messages;

/**
 * Системные сообщения
 */
public enum Messages {
    HELP("/help"),
    HELP_INFO(
            "/newChat ИМЯ_ЧАТА - создать новый чат\n" +
                    "/add ИМЯ_ПОЛЬЗОВАТЕЛЯ - добавить пользователя в чат"
    ),
    AUTH_SERVER_COMMAND_MESSAGE("/auth"),
    INCORRECT_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    USER_ALREADY_ONLINE("Этот пользователь уже онлайн!"),
    USER_CONNECTED(" пользователь подключился"),
    USER_DISCONNECTED(" пользователь отключился"),
    AUTH_OK("/authOk"),
    STOP_CLIENT_COMMAND_MESSAGE("/end"),
    STOP_SERVER_COMMAND_MESSAGE("/serverClosed"),
    CLIENTS_LIST_BROADCAST_COMMAND_MESSAGE("/clientslist "),
    BROADCAST_CHATS_LIST("/chatsList"),
    REG_COMMAND_MESSAGE("/reg"),
    REGISTRATED_COMMAND_MESSAGE("/regOk"),
    NOT_REGISTRATED_COMMAND_MESSAGE("/regNotOk"),
    BLACKLIST_MARK("/b"),
    CREATE_CHAT("/newChat"),
    BAD_CHAT_NAME("Чат не создан. В имени чата можно использовать только цифры и буквы!"),
    ADD_USER_TO_CHAT("/add"),
    USER_COME_IN_CHAT("В чат добавлен пользователь "),
    WELCOME("Вы успешно авторизовались! Добро пожаловать в чат!\nДля получения списка возможных команд введите /help\n"),
    PASSWORDS_DONT_EQUALS("Введеные пароли не совпадают\n");

    public final String msg;

    Messages(String msg) {
        this.msg = msg;
    }
}
