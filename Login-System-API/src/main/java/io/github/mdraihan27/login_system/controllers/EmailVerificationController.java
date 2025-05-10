package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.services.EmailVerificationService;
import io.github.mdraihan27.login_system.utilities.GetAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
public class EmailVerificationController {

    @Autowired
    private GetAuthenticatedUser getAuthenticatedUser;
    @Autowired
    private EmailVerificationService userVerificationService;


    @PostMapping("/verify/send-verification-code")
    public ResponseEntity sendVerificationCode() {
        try{
            Optional<UserEntity> userEntity = getAuthenticatedUser.GetAuthenticatedUser();
            if(userEntity.isPresent()){
                return userVerificationService.sendEmailVerificationCode(userEntity.get());

            }else{
                return ResponseEntity.badRequest().build();
            }

        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("verify/verify-verification-code")
    public ResponseEntity verifyVerificationCode(@RequestParam String verificationCode) {
        try{
            Optional<UserEntity> userEntity = getAuthenticatedUser.GetAuthenticatedUser();
            if(userEntity.isPresent()){
                return userVerificationService.verifyEmailVerificationCode(userEntity.get(), verificationCode);
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
