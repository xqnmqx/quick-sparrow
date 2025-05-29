package ru.quick.sparrow;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

public class Utils {

    public static String getSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String xor(String s1, String s2) {
        byte[] s1Bytes = s1.getBytes(StandardCharsets.UTF_8);
        byte[] s2Bytes = s2.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[s1Bytes.length];
        for (int i = 0; i < s1Bytes.length; i++) {
            result[i] = (byte) (s1Bytes[i] ^ s2Bytes[i]);
        }
        return new String(Base64.getEncoder().encode(result));
    }
    //53  -> 0011 0101 : 32 + 16 + 4 + 1,
    //54  -> 0011 0110 : 32 + 16 + 4 + 2,
    //xor -> 0000 0011 : 3 + 1

    //55  -> 0011 0111 : 32 + 16 + 4 + 2 + 1,
    //54  -> 0011 0110 : 32 + 16 + 4 + 2,
    //xor -> 0000 0001
}
