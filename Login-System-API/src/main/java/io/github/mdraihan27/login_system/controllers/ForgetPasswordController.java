package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.services.EmailService;
import io.github.mdraihan27.login_system.services.ForgotPasswordService;
import io.github.mdraihan27.login_system.services.UserService;
import io.github.mdraihan27.login_system.services.UserVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public")
public class ForgetPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserVerificationService userVerificationService;


    @PostMapping("send-forgot-password-code")
    public ResponseEntity sendForgotPasswordCodeUsingEmail (@RequestParam String emailOrUsername, @RequestParam String userinfoType) {
        try{
            ResponseEntity<UserEntity> response = userService.findUser(emailOrUsername, userinfoType);
            if(response.getStatusCode() == HttpStatus.OK){
               return forgotPasswordService.sendForgotPasswordVerificationCode(response.getBody());
            }else{
                return  ResponseEntity.badRequest().build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }


    @GetMapping("verify-forgot-password-verification-code")
    public ResponseEntity verifyVerificationCodeUsingEmail(@RequestParam String verificationCode, @RequestParam String emailOrUsername, @RequestParam String userinfoType) {
        try{
            ResponseEntity<UserEntity> response = userService.findUser(emailOrUsername, userinfoType);
            if(response.getStatusCode() == HttpStatus.OK){
                return userVerificationService.verifyVerificationCode(response.getBody(), verificationCode);
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }



}
