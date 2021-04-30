package common.data;

/**
 * Содержит информацию о подключении
 */
public class ConnectionData {

    private final static String IP_ADDRESS = "localhost";
    private final static int PORT = 8080;

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static int getPORT() {
        return PORT;
    }
}
