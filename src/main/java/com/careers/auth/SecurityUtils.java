package com.careers.auth;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final TextEncryptor encryptor;

    public SecurityUtils(@Value("${encryption.secret}") String secret) {
        this.encryptor = Encryptors.delux(secret, "deadbeef");
    }

    public String encrypt(String data) {
        return encryptor.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        return encryptor.decrypt(encryptedData);
    }
}