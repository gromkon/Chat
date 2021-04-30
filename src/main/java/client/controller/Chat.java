package client.controller;

import java.util.ArrayList;

/**
 * Содержит в себе информацию об одном из текущих чатов
 */
public class Chat {
    private String name;
    private String id;
    private ArrayList<String> messages;

    public Chat(String name, String id) {
        this.name = name;
        this.id = id;
        messages = new ArrayList<>();
    }

    /**
     * Добавляет сообщение в список сообщений
     * @param msg - сообщение
     */
    public void addMessage(String msg) {
        messages.add(msg);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
