package ru.quick.sparrow.service;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SparrowClient {
  private static final Logger logger = Logger.getLogger(SparrowClient.class.getName());

  private final MessengerGrpc.MessengerBlockingStub blockingStub;

  public SparrowClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = MessengerGrpc.newBlockingStub(channel);
  }

  public void post(String message, String address) {
    logger.info("Will try to post message: " + message + ". To address " + address);
    MessageRequest request = MessageRequest.newBuilder().setMessage(message).setAddress(address).build();
    MessageReply response;
    try {
      response = blockingStub.react(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Message sent: " + response.getMessage());
  }

  public List<String> getMessages(String address) {
    logger.info("Will try to request messages");
    MessagesRequest request = MessagesRequest.newBuilder().setAddress(address).build();
    MessagesResponse response;
    try {
      response = blockingStub.getMessages(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return List.of();
    }
    logger.info("Message sent: " + response.getMessages());
    return List.of(response.getMessages().getStrings(0));
  }

}
