package ru.quick.sparrow.service;

import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;
import ru.quick.sparrow.model.Message;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class MessengerImpl extends MessengerGrpc.MessengerImplBase {
    private static final Logger logger = Logger.getLogger(MessengerImpl.class.getName());

    private final Map<String, List<Message>> messages = new HashMap<>();

    @Override
    public void react(MessageRequest req, StreamObserver<MessageReply> responseObserver) {
      MessageReply reply = MessageReply.newBuilder().setMessage("Got message: " + req.getMessage()).build();
      putMessage(req.getAddress(), req.getMessage());
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void getMessages(MessagesRequest request, StreamObserver<MessagesResponse> responseStreamObserver) {
        logger.info("Getting messages for address: " + request.getClientId());
        List<Message> messages = getMessages(request.getClientId());
        List<MessageBody> messageBodies = messages.stream()
                .map(m -> MessageBody.newBuilder()
                        .setMessage(m.message())
                        .setTimestamp(m.dateTime().toEpochSecond(ZoneOffset.UTC)).build())
                .toList();
        MessagesResponse resp = MessagesResponse.newBuilder()
                .setMessages(ListOfMessages.newBuilder()
                        .addAllMessages(messageBodies)
                        .build())
                .build();
        responseStreamObserver.onNext(resp);
        responseStreamObserver.onCompleted();
    }

    private List<Message> getMessages(String clientId) {
        if (messages.containsKey(clientId)) {
            return messages.get(clientId);
        }
        return List.of(new Message(LocalDateTime.now(), "No messages for client with id: " + clientId));
    }

    private void putMessage(String address, String message) {
      logger.info("Message put: " + address + " " + message);
      if (messages.get(address) == null) {
        messages.put(address, Lists.newArrayList(new Message(LocalDateTime.now(), message)));
      } else {
        messages.get(address).add(new Message(LocalDateTime.now(), message));
      }
    }
  }