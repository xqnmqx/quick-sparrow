package ru.quick.sparrow.service;

import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class MessengerImpl extends MessengerGrpc.MessengerImplBase {
    private static final Logger logger = Logger.getLogger(MessengerImpl.class.getName());

    private final Map<String, List<String>> messages = new HashMap<>();

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
        String message = getMessage(request.getClientId());
        MessagesResponse resp = MessagesResponse.newBuilder()
                .setMessages(ListOfStrings.newBuilder()
                        .addStrings(message)
                        .build())
                .build();
        responseStreamObserver.onNext(resp);
        responseStreamObserver.onCompleted();
    }

    private String getMessage(String clientId) {
        if (messages.containsKey(clientId)) {
            return messages.get(clientId).get(0);
        }
        return "No messages for client with id: " + clientId;
    }

    private void putMessage(String address, String message) {
      logger.info("Message put: " + address + " " + message);
      if (messages.get(address) == null) {
        messages.put(address, Lists.newArrayList(message));
      } else {
        messages.get(address).add(message);
      }
    }
  }