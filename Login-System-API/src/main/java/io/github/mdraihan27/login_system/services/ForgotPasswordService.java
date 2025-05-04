package io.github.mdraihan27.login_system.services;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class ForgotPasswordService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    public ResponseEntity sendForgotPasswordVerificationCode(UserEntity userEntity) {
        try{
            String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
            emailService.sendEmail(userEntity.getEmail(), "Your verification Code", verificationCode);
            userEntity.setVerificationCode(verificationCode);
            userEntity.setVerificationCodeExpirationTime(Instant.now().plus(Duration.ofMinutes(5)));
            userRepository.save(userEntity);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

//    @Transactional
//    public ResponseEntity verifyForgotPasswordVerificationCode(UserEntity userEntity, String verificationCode) {
//        try{
//            if(userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isAfter(Instant.now())){
//                userEntity.setVerificationCode("");
//                userEntity.setVerified(true);
//                userRepository.save(userEntity);
//
//                return ResponseEntity.ok().build();
//
//            }else if(userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isBefore(Instant.now())){
//                return ResponseEntity.unprocessableEntity().build();
//
//            }else{
//                return ResponseEntity.badRequest().build();
//            }
//        }catch (Exception e){
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
