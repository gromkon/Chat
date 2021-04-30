package server.server_handler;

import common.data.ConnectionData;

/**
 * Запускает сервер
 */
public class ServerStart {

    public static void main(String[] args) {
        new Server(ConnectionData.getPORT());
    }

}
