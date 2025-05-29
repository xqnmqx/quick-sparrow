package ru.quick.sparrow.service;

import static ru.quick.sparrow.Utils.getSHA256;
import static ru.quick.sparrow.Utils.xor;

public class UtilsTest {

    public static void main(String[] args) {
        String client1 = "client-1";
        String client2 = "client-2";
        String client3 = "client-3";

        String h1 = getSHA256(client1);
        String h2 = getSHA256(client2);
        String h3 = getSHA256(client3);

        String xResult1 = xor(h1, h2);
        System.out.println(xResult1);

        String xResult2 = xor(h2, h1);
        System.out.println(xResult2);

        String xResult3 = xor(h2, h3);
        System.out.println(xResult3);

        String xResult4 = xor(h3, h2);
        System.out.println(xResult4);

        String xResult5 = xor(h1, h3);
        System.out.println(xResult5);

        String xResult6 = xor(h3, h1);
        System.out.println(xResult6);
    }
}
