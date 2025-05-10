package io.github.mdraihan27.login_system.services;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    public ResponseEntity sendEmailVerificationCode(UserEntity userEntity) throws Exception {

        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        emailService.sendEmail(userEntity.getEmail(), "Your verification Code", verificationCode, "Use this code to verify your email address");
        userEntity.setVerificationCode(verificationCode);
        userEntity.setVerificationCodeExpirationTime(Instant.now().plus(Duration.ofMinutes(5)));
        userRepository.save(userEntity);
        return ResponseEntity.ok().build();

    }


    public ResponseEntity verifyEmailVerificationCode(UserEntity userEntity, String verificationCode) throws Exception {

        if(!userEntity.getVerificationCode().equals("") && userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isAfter(Instant.now())){
            userEntity.setVerificationCode("");
            userEntity.setVerified(true);
            userRepository.save(userEntity);

            return ResponseEntity.ok().build();

        }else if(userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isBefore(Instant.now())){
            return ResponseEntity.unprocessableEntity().build();

        }else{
            return ResponseEntity.badRequest().build();
        }

    }
}
