package ru.quick.sparrow.store;

import ru.quick.sparrow.model.Message;

import java.util.List;

public interface MessageStore {

    void putMessage(String addressee, String message, String sender);

    List<Message> getMessages(String clientId, String friendId);
}
