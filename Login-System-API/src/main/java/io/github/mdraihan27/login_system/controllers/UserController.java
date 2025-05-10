package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.services.UserService;
import io.github.mdraihan27.login_system.utilities.GetAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GetAuthenticatedUser getAuthenticatedUser;

    @GetMapping("/user/logged-in-user-info")
    public ResponseEntity<UserEntity> getLoggedInUserInfo() {
        try{
            Optional<UserEntity> authenticatedUser =  getAuthenticatedUser.GetAuthenticatedUser();
            if(authenticatedUser.isPresent()) {
                return ResponseEntity.ok(authenticatedUser.get());
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("user/{username}")
    public ResponseEntity<UserEntity> getUserInfo(@PathVariable String username) {
        try{
            return userService.findUser(username, "username");
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
