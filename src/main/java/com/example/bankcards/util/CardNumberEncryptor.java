package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Converter
@Component
public class CardNumberEncryptor implements AttributeConverter<String, byte[]> {

    private final String algorithm;

    private final SecretKeySpec secretKeySpec;

    public CardNumberEncryptor(@Value("${card-encryption.secret}") String secret,
                               @Value("${card-encryption.algorithm}") String algorithm) {
        this.secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
        this.algorithm = algorithm;
    }

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        if (dbData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(dbData);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}
