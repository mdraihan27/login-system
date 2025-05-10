package io.github.mdraihan27.login_system.services;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.repositories.UserRepository;
import io.github.mdraihan27.login_system.utilities.GetAuthenticatedUser;
import io.github.mdraihan27.login_system.utilities.JwtUtil;
import io.jsonwebtoken.security.Password;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ForgotPasswordService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GetAuthenticatedUser getAuthenticatedUser;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;


    public ResponseEntity sendForgotPasswordVerificationCode(UserEntity userEntity) throws Exception {

        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        emailService.sendEmail(userEntity.getEmail(), "Your verification code", verificationCode, "Use this code to continue with resetting your password");
        userEntity.setVerificationCode(verificationCode);
        userEntity.setVerificationCodeExpirationTime(Instant.now().plus(Duration.ofMinutes(5)));
        userRepository.save(userEntity);
        return ResponseEntity.ok().build();

    }

    public ResponseEntity<String> verifyForgotPasswordVerificationCode(UserEntity userEntity, String verificationCode) throws Exception {

        if(!userEntity.getVerificationCode().equals("") && userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isAfter(Instant.now())){
            userEntity.setVerificationCode("");
            userEntity.setVerified(true);
            userRepository.save(userEntity);
            String jwt = jwtUtil.generateToken(userEntity.getUsername(), false);
            return ResponseEntity.ok(jwt);

        }else if(userEntity.getVerificationCode().equals(verificationCode) && userEntity.getVerificationCodeExpirationTime().isBefore(Instant.now())){
            return ResponseEntity.unprocessableEntity().build();

        }else{
            return ResponseEntity.badRequest().build();
        }

    }

    public ResponseEntity resetPassword(String newPassword) throws Exception {
        Optional<UserEntity> authenticatedUserEntity = getAuthenticatedUser.GetAuthenticatedUser();
        if (authenticatedUserEntity.isPresent()) {
           authenticatedUserEntity.get().setPassword(passwordEncoder.encode(newPassword));
           userRepository.save(authenticatedUserEntity.get());
           return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
