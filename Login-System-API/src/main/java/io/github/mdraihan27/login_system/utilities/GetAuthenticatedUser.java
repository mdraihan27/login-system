package io.github.mdraihan27.login_system.utilities;

import io.github.mdraihan27.login_system.entities.UserEntity;
import io.github.mdraihan27.login_system.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class GetAuthenticatedUser {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserEntity> GetAuthenticatedUser() {
       try{
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           String username = authentication.getName();
           return userRepository.findByUsername(username);
       }catch(Exception e){
           log.error(e.getMessage());
           return Optional.empty();
       }
    }
}
