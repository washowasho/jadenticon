package com.github.atomfrede.jadenticon;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Hash {

    static String generateHash(String text) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            byte byteData[] = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't find algorithm 'SHA-256'", e);
        }

    }
}
