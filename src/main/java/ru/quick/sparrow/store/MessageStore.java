package ru.quick.sparrow.store;

import ru.quick.sparrow.model.Message;

import java.util.List;

public interface MessageStore {

    void putMessage(String messageBox, String message);

    List<Message> getMessages(String clientId);
}
