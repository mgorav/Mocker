package com.gm.virtualization.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.getInstance;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public abstract  class HashKeyUtil {

    public static String getHashKey(String string) {
        if (string == null) {
            string = "";
        }
        MessageDigest md = null;
        try {
            md = getInstance("MD5");
            md.update(string.getBytes());
            byte[] digest = md.digest();
            return printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
