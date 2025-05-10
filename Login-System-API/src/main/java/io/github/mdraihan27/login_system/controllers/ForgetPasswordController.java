package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.services.ForgotPasswordService;
import io.github.mdraihan27.login_system.services.UserService;
import io.github.mdraihan27.login_system.services.EmailVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController

public class ForgetPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailVerificationService userVerificationService;


    @PostMapping("/auth/send-forgot-password-code")
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


    @GetMapping("/auth/verify-forgot-password-verification-code")
    public ResponseEntity<String> verifyVerificationCodeUsingEmail(@RequestParam String verificationCode, @RequestParam String emailOrUsername, @RequestParam String userinfoType) {
        try{
            ResponseEntity<UserEntity> response = userService.findUser(emailOrUsername, userinfoType);
            if(response.getStatusCode() == HttpStatus.OK){
                return forgotPasswordService.verifyForgotPasswordVerificationCode(response.getBody(), verificationCode);
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/user/resetPassword")
    public ResponseEntity resetPassword(@RequestParam("newPassword") String newPassword) {
        try{
            return forgotPasswordService.resetPassword(newPassword);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }






}
