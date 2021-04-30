package common.status;

/**
 * Статус регистрации
 */
public enum RegStatus {
    NICKNAME_USED(" Данный никнейм занят\n"),
    LOGIN_USED(" Данный логин занят\n"),
    OK(" Вы успешно зарегестрировались\n"),
    ERROR(" Произошла ошибка, попробуйте еще раз\n");


    public final String answer;

    RegStatus(String answer) {
        this.answer = answer;
    }

}
