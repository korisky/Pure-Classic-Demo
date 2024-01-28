package com.own.spring.demo.util;

import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionDecryptionTest {
    @Test
    public void test() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        final String priKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
        final String pubKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());

        System.out.println("Pri: " + priKey);
        System.out.println("Pub: " + pubKey);
        System.out.println();

        String text = "Secret !!!";
        final String encrypted = RSAUtil.encryptString(pubKey, text).get();
        System.out.println("Encrypted: " + encrypted);
        System.out.println();

        final String decrypted = RSAUtil.decryptString(priKey, encrypted).get();
        System.out.println("Decrypted: " + decrypted);
    }
}
