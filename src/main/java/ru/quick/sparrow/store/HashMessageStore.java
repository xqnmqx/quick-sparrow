package ru.quick.sparrow.store;

import com.google.common.collect.Lists;
import ru.quick.sparrow.model.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HashMessageStore implements MessageStore {

    private static final Logger logger = Logger.getLogger(HashMessageStore.class.getName());
    private final Map<String, List<Message>> messages = new HashMap<>();

    @Override
    public void putMessage(String messageBox, String message) {
        logger.info("Message put: " + messageBox + " " + message);
        if (messages.get(messageBox) == null) {
            messages.put(messageBox, Lists.newArrayList(new Message(LocalDateTime.now(), message)));
        } else {
            messages.get(messageBox).add(new Message(LocalDateTime.now(), message));
        }
    }

    @Override
    public List<Message> getMessages(String clientId) {
        if (messages.containsKey(clientId)) {
            return messages.get(clientId);
        }
        return List.of(new Message(LocalDateTime.now(), "No messages for client with id: " + clientId));
    }
}
