package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.services.UserService;
import io.github.mdraihan27.login_system.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager ;

    @Autowired
    private UserDetailsService userDetailsService;

//    @Transactional
    @PostMapping("signup")
    public ResponseEntity<String> signup (@RequestBody UserEntity userEntity) throws Exception {
        try{
            if(!userEntity.getEmail().isEmpty() && !userEntity.getPassword().isEmpty() && !userEntity.getEmail().isEmpty()
                    && !userEntity.getFirstName().isEmpty() && !userEntity.getLastName().isEmpty() ){

                ResponseEntity<UserEntity> response1 = userService.findUser(userEntity.getUsername(), "username");
                ResponseEntity<UserEntity> response2 = userService.findUser(userEntity.getEmail(), "email");

                if(response1.getStatusCode().equals(HttpStatus.OK) || response2.getStatusCode().equals(HttpStatus.OK)){
                    return ResponseEntity.unprocessableEntity().build();
                }

                UserEntity createdUser = userService.createUser(userEntity);
                if(createdUser != null) {
                    String jwt = jwtUtil.generateToken(createdUser.getUsername());
                    return ResponseEntity.ok(jwt);
                }else {
                    return ResponseEntity.internalServerError().build();
                }
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            //Exception needs to be thrown if @Transactional is used, to make transaction manager aware that it needs to rollback
//            throw new Exception("Something went wrong while signing up user");
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("login")
    public ResponseEntity<String> login (@RequestParam("emailOrUsername") String emailOrUsername, @RequestParam("password") String password, @RequestParam("userinfoType") String userinfoType) {
        try{
            if(!emailOrUsername.isEmpty() && !password.isEmpty() && !userinfoType.isEmpty()) {
               return checkAuthAndGenerateJwt(emailOrUsername, password, userinfoType);

            }else{
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

//    @GetMapping("login-email")
//    public ResponseEntity<String> loginEmail (@RequestParam("email") String email, @RequestParam("password") String password) {
//        try{
//            if(!email.isEmpty() && !password.isEmpty()) {
//                UserEntity userEntity = userService.findUser(email, "email").getBody();
//                if(userEntity != null) {
//                    return checkAuthAndGenerateJwt(userEntity.getUsername(), password);
//                }else{
//                    return ResponseEntity.badRequest().build();
//                }
//
//            }else{
//                return ResponseEntity.badRequest().build();
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }



    private ResponseEntity<String> checkAuthAndGenerateJwt(String emailOrUsername, String password, String userinfoType) {

        try{
            String username;
            if(userinfoType.equals("email")){
                ResponseEntity<UserEntity> response1 = userService.findUser(emailOrUsername, "email");
                if(response1.getStatusCode().equals(HttpStatus.OK)) {
                    username = response1.getBody().getUsername();
                }else{
                    return ResponseEntity.badRequest().build();
                }
            }else if(userinfoType.equals("username")){
                username = emailOrUsername;
            }else{
                return ResponseEntity.badRequest().build();
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            if(jwt != null) {
                return ResponseEntity.ok(jwt);
            }else{
                return ResponseEntity.internalServerError().build();
            }

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
