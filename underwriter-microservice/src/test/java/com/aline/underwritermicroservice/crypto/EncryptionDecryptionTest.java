package com.aline.underwritermicroservice.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;

@ActiveProfiles("test")
@SpringBootTest
public class EncryptionDecryptionTest {

    @Autowired
    @Qualifier("ENCRYPT_MODE")
    Cipher encrypt;

    @Autowired
    @Qualifier("DECRYPT_MODE")
    Cipher decrypt;

    @Test
    void test_encryptToDecrypt() throws Exception {

        String data = "This is some data.";

        byte[] encryptedBytes = encrypt.doFinal(data.getBytes(StandardCharsets.UTF_8));

        assertThat(data.getBytes(StandardCharsets.UTF_8), not(equalTo(encryptedBytes)));

        byte[] decryptedBytes = decrypt.doFinal(encryptedBytes);

        assertThat(decryptedBytes, equalTo(data.getBytes(StandardCharsets.UTF_8)));

        String decryptedData = new String(decryptedBytes);

        assertThat(data, equalTo(decryptedData));

    }

}
