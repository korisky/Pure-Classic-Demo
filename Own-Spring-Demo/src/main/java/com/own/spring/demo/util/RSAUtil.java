package com.own.spring.demo.util;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

/**
 * RAS util
 */
public class RSAUtil {

    /**
     * Encrypts a string using an RSA public key.
     *
     * @param rsaPubKey Public key in Base64 encoded format.
     * @param plainText Text to be encrypted.
     * @return Optional containing the encrypted text in Base64 format, or empty if an error occurs.
     */
    public static Optional<String> encryptString(String rsaPubKey, String plainText) {
        try {
            PublicKey publicKey = loadPublicKey(rsaPubKey);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Optional.of(Base64.getEncoder().encodeToString(encryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Decrypts a string using an RSA private key.
     *
     * @param rsaPriKey Private key in Base64 encoded format.
     * @param encryptedText Base64 encoded encrypted text.
     * @return Optional containing the decrypted text, or empty if an error occurs.
     */
    public static Optional<String> decryptString(String rsaPriKey, String encryptedText) {
        try {
            PrivateKey privateKey = loadPrivateKey(rsaPriKey);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return Optional.of(new String(decryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static PublicKey loadPublicKey(String key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static PrivateKey loadPrivateKey(String key) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

}
