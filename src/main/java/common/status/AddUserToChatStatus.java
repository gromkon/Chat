package common.status;

/**
 * Статус добавления пользователя в чат
 */
public enum AddUserToChatStatus {
    OK("Пользователь добавлен в чат"),
    NO_USER("Такого пользователя не существует"),
    ALREADY_IN_CHAT("Пользователь уже в чате"),
    ERROR("Произошла ошибка при добавлении пользователя в чат, попробуйте еще раз");

    public final String answer;

    AddUserToChatStatus(String answer) {
        this.answer = answer;
    }
}
