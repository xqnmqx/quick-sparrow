package ru.quick.sparrow.store;

import com.google.common.collect.Lists;
import ru.quick.sparrow.model.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ru.quick.sparrow.Utils.getSHA256;
import static ru.quick.sparrow.Utils.xor;

public class HashMessageStore implements MessageStore {

    private static final Logger logger = Logger.getLogger(HashMessageStore.class.getName());
    private final Map<String, List<Message>> messages = new HashMap<>();

    @Override
    public void putMessage(String addressee, String message, String sender) {
        logger.info("Message put: " + addressee + " " + message);
        String uniqueConversationKey = generateUniqueConversationKey(sender, addressee);
        logger.info("$$ Unique conversation key: " + uniqueConversationKey);
        if (messages.get(uniqueConversationKey) == null) {
            messages.put(uniqueConversationKey, Lists.newArrayList(new Message(LocalDateTime.now(), message, sender, addressee)));
        } else {
            messages.get(uniqueConversationKey).add(new Message(LocalDateTime.now(), message, sender, addressee));
        }
    }

    @Override
    public List<Message> getMessages(String clientId, String friendId) {
        if (messages.containsKey(clientId)) {
            return messages.get(clientId);
        }
        return List.of(
                new Message(
                        LocalDateTime.now(),
                        "No messages for client with id: " + clientId,
                        "",
                        ""));
    }

    private String generateUniqueConversationKey(String sender, String addressee) {
        String senderHash = getSHA256(sender);
        String addressHash = getSHA256(addressee);
        return xor(senderHash, addressHash);
    }

}
