package ru.quick.sparrow.service;

import io.grpc.stub.StreamObserver;
import ru.quick.sparrow.model.Message;
import ru.quick.sparrow.store.HashMessageStore;
import ru.quick.sparrow.store.MessageStore;

import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;

class MessengerImpl extends MessengerGrpc.MessengerImplBase {
    private static final Logger logger = Logger.getLogger(MessengerImpl.class.getName());

    private final MessageStore messageStore;

    public MessengerImpl() {
        messageStore = new HashMessageStore();
    }

    @Override
    public void react(MessageRequest req, StreamObserver<MessageReply> responseObserver) {
      MessageReply reply = MessageReply.newBuilder().setMessage("Got message: " + req.getMessage()).build();
      messageStore.putMessage(req.getAddress(), req.getMessage(), req.getClientId());
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void getMessages(MessagesRequest request, StreamObserver<MessagesResponse> responseStreamObserver) {
        logger.info("Getting messages for address: " + request.getClientId());
        List<Message> messages = messageStore.getMessages(request.getClientId(), request.getFriendId());
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
  }