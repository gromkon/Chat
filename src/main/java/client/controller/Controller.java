package client.controller;

import common.data.ConnectionData;
import common.data.GeneralChatData;
import common.info.ChatInfo;
import common.system_messages.Messages;
import common.utils.MsgGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ComboBox comboBox;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    HBox bottomPanel;
    @FXML
    HBox upperPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    ListView<String> clientsList;

    @FXML
    HBox regPanel;
    @FXML
    TextField regNicknameField;
    @FXML
    TextField regLoginField;
    @FXML
    PasswordField regPasswordField;
    @FXML
    PasswordField regCheckPasswordField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String currentChatId;
    private ArrayList<Chat> chats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentChatId = GeneralChatData.getGeneralChatIdString();
        setAuth(false);
        initChatList();
        connect();
    }

    /**
     * Инициализирует список чатов, в которых состоит пользователь
     */
    private void initChatList() {
        chats = new ArrayList<>();
        Chat generalChat = new Chat(GeneralChatData.getGeneralChatName(), GeneralChatData.getGeneralChatIdString());
        generalChat.addMessage(Messages.WELCOME.msg);
        chats.add(generalChat);
        ObservableList<String> list = FXCollections.observableArrayList(GeneralChatData.getGeneralChatName());
        comboBox.setItems(list);
        comboBox.setValue(GeneralChatData.getGeneralChatName());
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (comboBox.getValue() != null) {
                    String chatName = (String) comboBox.getValue();
                    textArea.clear();
                    for (Chat chat: chats) {
                        if (chatName.equals(chat.getName())) {
                            currentChatId = chat.getId();
//                            ArrayList<String> messages = chat.getMessages();
                            for (String msg : chat.getMessages()) {
                                textArea.appendText(msg);
                            }
//                            for (String m: messages) {
//                                textArea.appendText(m);
//                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Изменяет список чатов, в которых состоит пользователь
     * @param chatList - список чатов
     */
    public void setChatList(ArrayList<Chat> chatList) {
        int currentChatPos = saveData(chatList);
        String[] cl = new String[chatList.size()];
        for (int i = 0; i < chatList.size(); i++) {
            cl[i] = chatList.get(i).getName();
        }
        ObservableList<String> list = FXCollections.observableArrayList(cl);
        Platform.runLater(
                () -> {
                    comboBox.setItems(list);
                    comboBox.setValue(cl[currentChatPos]);
                }
        );
    }

    /**
     * Сохраняет сообщения из старого списка чатов в новый список чатов
     * @param chatList - новый список чатов
     * @return - возвращает позицию чата из которого было написано сообщение в списке чатов, если такого чата больше нет, возвращает 0
     */
    private int saveData(ArrayList<Chat> chatList) {
        int count = 0;
        int currentChatPos = 0;
        for (Chat c1: chatList) {
            for (Chat c2: chats) {
                if (c1.getId().equals(c2.getId())) {
                    c1.setMessages(c2.getMessages());
                }
            }
            if (c1.getId().equals(currentChatId)) {
                currentChatPos = count;
            }
            count++;
        }
        chats = chatList;
        return currentChatPos;
    }

    /**
     * Добавляет сообщение в конкретный чат. Если этот чат активен, добавляет его в поле ввода
     * @param chatId - ID чата
     * @param msg - сообщение
     */
    public void addMessage(String chatId, String msg) {
        for (Chat c: chats) {
            if (c.getId().equals(chatId)) {
                c.addMessage(msg);
                if (currentChatId.equals(chatId)) {
                    textArea.appendText(msg);
                }
                break;
            }
        }
    }

    /**
     * Изменяет видимость объектов
     * @param isAuth - авторизован ли пользователь
     */
    public void setAuth(boolean isAuth) {
        if (isAuth) {
            upperPanelSetVisibleAndManaged(false);
            regPanelSetVisibleAndManaged(false);
            bottomPanelSetVisibleAndManaged(true);
            clientsListSetVisibleAndManaged(false); //true
        } else {
            upperPanelSetVisibleAndManaged(true);
            regPanelSetVisibleAndManaged(false);
            bottomPanelSetVisibleAndManaged(false);
            clientsListSetVisibleAndManaged(false);
        }
    }

    /**
     * Переключает между панелями авторизации и регистрации
     */
    private void regPanelSetActive(boolean flag) {
        if (flag) {
            upperPanelSetVisibleAndManaged(false);
            regPanelSetVisibleAndManaged(true);
        } else {
            upperPanelSetVisibleAndManaged(true);
            regPanelSetVisibleAndManaged(false);
        }
    }

    /**
     * Включает панель регистрации
     */
    public void activeRegPanel() {
        regPanelSetActive(true);
    }

    /**
     * Отключает панель регистрации
     */
    public void disableRegPanel() {
        regPanelSetActive(false);
    }

    /**
     * Включает/отключает панель авторизации
     */
    private void upperPanelSetVisibleAndManaged(boolean flag) {
        upperPanel.setVisible(flag);
        upperPanel.setManaged(flag);
    }

    /**
     * Включает/отключает панель авторизации
     */
    private void regPanelSetVisibleAndManaged(boolean flag) {
        regPanel.setVisible(flag);
        regPanel.setManaged(flag);
    }

    /**
     * Включает/отключает панель чата
     */
    private void bottomPanelSetVisibleAndManaged(boolean flag) {
        bottomPanel.setVisible(flag);
        bottomPanel.setManaged(flag);
    }

    // TODO удалить
    private void clientsListSetVisibleAndManaged(boolean flag) {
        clientsList.setVisible(flag);
        clientsList.setManaged(flag);
    }

    /**
     * Осуществляет подключение к серверу и инициализацию сокета, входящего и исходящего потока сообщений
     */
    private void connect() {
        try {
            socket = new Socket(ConnectionData.getIpAddress(), ConnectionData.getPORT());
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            MessageHandler messageHandler = new MessageHandler(socket, in, out, this);
            messageHandler.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * При завершении работы программы отправляет на сервер сообщение о том, что клиент отключился
     */
    public void dispose() {
        if (out != null) {
            try {
                out.writeUTF(Messages.STOP_CLIENT_COMMAND_MESSAGE.msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Отправляет сообщение серверу
     * Сообщение - это текст, который находится в панеле @textField, сразу после отправки сообщения, текст из панели очищается
     */
    public void sendMsg(ActionEvent actionEvent) {
        try {
            String msg = MsgGenerator.generateMessageToChat(Integer.parseInt(currentChatId), textField.getText());
            out.writeUTF(msg);
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Попытка авторизации пользователя
     * После авторизации поля с логином и паролем очищаются
     */
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF(Messages.AUTH_SERVER_COMMAND_MESSAGE.msg + " " + loginField.getText() + " " + passwordField.getText()); //TODO .hasCode()
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Попытка регистрации пользователя
     */
    public void regUser() {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            String nickname = regNicknameField.getText();
            String login = regLoginField.getText();
            String pass = regPasswordField.getText(); // TODO hashCode()
            String passCheck = regCheckPasswordField.getText(); // TODO hashCode()
            if (pass.equals(passCheck)) {
                out.writeUTF(Messages.REG_COMMAND_MESSAGE.msg + " " + nickname + " " + login + " " + pass);
            } else {
                textArea.appendText(Messages.PASSWORDS_DONT_EQUALS.msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}

