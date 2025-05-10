package io.github.mdraihan27.login_system.controllers;

import io.github.mdraihan27.login_system.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RefreshJwtController {
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/public/refresh-jwt")
    public ResponseEntity<String> refreshJwt(@RequestParam("refreshToken") String refreshToken) {
        try{
            if(jwtUtil.validateToken(refreshToken, true)){
                String username = jwtUtil.extractUsername(refreshToken, true);
                String newJwt = jwtUtil.generateToken(username, false);
                return new ResponseEntity<>(newJwt, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
