package ru.quick.sparrow.service;

import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;
import ru.quick.sparrow.model.Message;
import ru.quick.sparrow.store.HashMessageStore;
import ru.quick.sparrow.store.MessageStore;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class MessengerImpl extends MessengerGrpc.MessengerImplBase {
    private static final Logger logger = Logger.getLogger(MessengerImpl.class.getName());

    private final MessageStore messageStore;

    public MessengerImpl() {
        messageStore = new HashMessageStore();
    }

    @Override
    public void react(MessageRequest req, StreamObserver<MessageReply> responseObserver) {
      MessageReply reply = MessageReply.newBuilder().setMessage("Got message: " + req.getMessage()).build();
      messageStore.putMessage(req.getAddress(), req.getMessage());
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override
    public void getMessages(MessagesRequest request, StreamObserver<MessagesResponse> responseStreamObserver) {
        logger.info("Getting messages for address: " + request.getClientId());
        List<Message> messages = messageStore.getMessages(request.getClientId());
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