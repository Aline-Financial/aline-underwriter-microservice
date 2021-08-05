package com.aline.underwritermicroservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Provides cryptography beans
 */
@Configuration
public class CryptoConfig {

    @Value("${application.crypto.secret-key}")
    private String secretKey;

    /**
     * Simple AES Cipher AES in Decrypt Mode
     * @return Instance of AES Cipher class
     * @throws NoSuchPaddingException Thrown by {@link Cipher#getInstance(String)}
     * @throws NoSuchAlgorithmException Thrown by {@link Cipher#getInstance(String)}
     */
    @Bean(name = "DECRYPT_MODE")
    public Cipher aesDecrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher aes = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        aes.init(Cipher.DECRYPT_MODE, key);
        return aes;
    }

    /**
     * Simple AES Cipher AES in Encrypt Mode
     * @return Instance of AES Cipher class
     * @throws NoSuchPaddingException Thrown by {@link Cipher#getInstance(String)}
     * @throws NoSuchAlgorithmException Thrown by {@link Cipher#getInstance(String)}
     * @throws InvalidKeyException Thrown by {@link SecretKeySpec}
     */
    @Bean(name = "ENCRYPT_MODE")
    public Cipher aesEncrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher aes = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        aes.init(Cipher.ENCRYPT_MODE, key);
        return aes;
    }

}
