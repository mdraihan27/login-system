package io.github.mdraihan27.login_system.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;

@Document(collection = "user")
@Getter
@Setter
public class UserEntity {

    @Id
    @NonNull
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String username;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;

    private String firstName;
    private String lastName;

    private boolean verified;
    private String verificationCode;
    private Instant verificationCodeExpirationTime;
    private boolean enabled;

    private ArrayList<String> roles=new ArrayList<>();

}
