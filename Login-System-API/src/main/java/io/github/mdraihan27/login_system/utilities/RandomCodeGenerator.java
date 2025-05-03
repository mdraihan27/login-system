package io.github.mdraihan27.login_system.utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Component
public class RandomCodeGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new SecureRandom();

    public String generateUUID(int length) {
        if (length <= 0) throw new IllegalArgumentException("Length must be greater than 0");
        StringBuilder uuid = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            uuid.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return uuid.toString();
    }
}
