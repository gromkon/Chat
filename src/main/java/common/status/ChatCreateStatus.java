package common.status;

/**
 * Статус создания чата
 */
public enum ChatCreateStatus {
    OK("Чат успешно создан"),
    WRONG_NAME("Вы не можете создать 2 чата с одинаковым именем"),
    ERROR("Произошла ошибка при создании чата, попробуйте еще раз");

    public final String answer;

    ChatCreateStatus(String answer) {
        this.answer = answer;
    }
}
