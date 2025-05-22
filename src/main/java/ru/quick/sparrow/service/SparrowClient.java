package ru.quick.sparrow.service;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link SparrowServer}.
 */
public class SparrowClient {
  private static final Logger logger = Logger.getLogger(SparrowClient.class.getName());

  private final MessengerGrpc.MessengerBlockingStub blockingStub;

  /** Construct client for accessing HelloWorld server using the existing channel. */
  public SparrowClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = MessengerGrpc.newBlockingStub(channel);
  }

  /** Say hello to server. */
  public void post(String message) {
    logger.info("Will try to post message: " + message + " ...");
    MessageRequest request = MessageRequest.newBuilder().setName(message).build();
    MessageReply response;
    try {
      response = blockingStub.react(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Message sent: " + response.getMessage());
  }

}
