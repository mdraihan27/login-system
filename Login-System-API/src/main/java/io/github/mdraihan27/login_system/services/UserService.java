package io.github.mdraihan27.login_system.services;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.repositories.UserRepository;
import io.github.mdraihan27.login_system.utilities.RandomCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RandomCodeGenerator randomCodeGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<UserEntity> findUser(String userinfo, String infoType) {

        try{
            Optional<UserEntity> user;

            if(infoType.equals("email")){
                user = userRepository.findByEmail(userinfo);
            }else if(infoType.equals("username")){
                user = userRepository.findByUsername(userinfo);
            }else if(infoType.equals("id")){
                user = userRepository.findById(userinfo);
            }else{
                throw new Exception("User info type is not valid");
            }

            if(user.isPresent()){
                return ResponseEntity.ok(user.get());
            }else{
                return ResponseEntity.notFound().build();
            }

        }catch (Exception e){

            log.error(e.getMessage());
            return ResponseEntity.notFound().build();

        }
    }

    public UserEntity createUser(UserEntity userEntity) {
        try{
            userEntity.setEnabled(true);
            userEntity.setId(UUID.randomUUID().toString().replace("-", ""));
            userEntity.setVerified(false);
            userEntity.setVerificationCode("");
            userEntity.setVerificationCodeExpirationTime(Instant.now());
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            ArrayList<String> roles = userEntity.getRoles();
            roles.add("USER");
            userEntity.setRoles(roles);
            UserEntity createdUser = userRepository.save(userEntity);

            if(createdUser != null){
                return createdUser;
            }else{
                return null;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }



}
