package ru.quick.sparrow.service;

import com.google.common.collect.Lists;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MessengerImpl extends MessengerGrpc.MessengerImplBase {

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
        System.out.println("Getting messages for address: " + request.getAddress());
        String message = messages.get(request.getAddress()).get(0);
        MessagesResponse resp = MessagesResponse.newBuilder()
                .setMessages(ListOfStrings.newBuilder()
                        .addStrings(message)
                        .build())
                .build();
        responseStreamObserver.onNext(resp);
        responseStreamObserver.onCompleted();
    }

    public void putMessage(String address, String message) {
      System.out.println("Message put: " + address + " " + message);
      if (messages.get(address) == null) {
        messages.put(address, Lists.newArrayList(message));
      } else {
        messages.get(address).add(message);
      }
    }
  }