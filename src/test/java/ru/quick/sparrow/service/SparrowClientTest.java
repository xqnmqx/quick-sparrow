package ru.quick.sparrow.service;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SparrowClientTest {
    private static final Logger logger = Logger.getLogger(SparrowClientTest.class.getName());
    public static final String CLIENT_1 = "client-1";
    public static final String CLIENT_2 = "client-2";

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        String user = "sparrow";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]]");
                System.err.println("");
                System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.exit(1);
            }
            user = args[0];
        }
        if (args.length > 1) {
            target = args[1];
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        //TODO: TLS
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        try {
            SparrowClient client1 = new SparrowClient(channel, CLIENT_1);
            SparrowClient client2 = new SparrowClient(channel, CLIENT_2);

            client1.post("Hi! How are you?", CLIENT_2);
            readClientMessages(client2, client1);

            client2.post("Hi! Thanks! I am fine. And what about you?", CLIENT_1);
            readClientMessages(client1, client2);

            client1.post("I am good. Do you have a plan on the evening today?", CLIENT_2);
            readClientMessages(client2, client1);

            client2.post("No. But what do you want?", CLIENT_1);
            readClientMessages(client1, client2);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private static void readClientMessages(SparrowClient sparrowClient, SparrowClient friend) {
        ListOfMessages listOfMessages = (ListOfMessages) sparrowClient.getMessages(friend.getClientId()).get(0);
        listOfMessages.getMessagesList().forEach(m -> {
            logger.info(
                    "Response messages for client"
                            + sparrowClient.getClientId()
                            + ": " + m.getTimestamp()
                            + " : " + m.getMessage());
        });
    }
}
