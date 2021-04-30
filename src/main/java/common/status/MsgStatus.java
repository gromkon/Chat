package common.status;

/**
 * Статус отправки сообщения
 */
public enum MsgStatus {
    OK("Сообщение отправлено"),
    ERROR("Произошла ошибка при отправке сообщения, попробуйте еще раз");

    public final String answer;

    MsgStatus(String answer) {
        this.answer = answer;
    }
}
