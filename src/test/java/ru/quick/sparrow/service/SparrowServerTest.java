package ru.quick.sparrow.service;

import java.io.IOException;

public class SparrowServerTest {

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final SparrowServer server = new SparrowServer();
        server.start();
        server.blockUntilShutdown();
    }

}
