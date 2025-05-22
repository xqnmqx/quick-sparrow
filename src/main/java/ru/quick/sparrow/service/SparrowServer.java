package ru.quick.sparrow.service;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SparrowServer {

  private static final Logger logger = Logger.getLogger(SparrowServer.class.getName());
  private static final int SERVER_PORT = 50051;

  private Server server;

  public void start() throws IOException {
    server = Grpc.newServerBuilderForPort(SERVER_PORT, InsecureServerCredentials.create())
        .addService(new MessengerImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + SERVER_PORT);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          SparrowServer.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
