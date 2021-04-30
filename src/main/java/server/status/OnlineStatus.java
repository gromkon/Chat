package server.status;

/**
 * Содержит онлайн и оффлайн статусы
 */
public class OnlineStatus {

    private final static int ONLINE = 1;
    private final static int OFFLINE = 0;

    public static int getONLINE() {
        return ONLINE;
    }

    public static int getOFFLINE() {
        return OFFLINE;
    }
}
