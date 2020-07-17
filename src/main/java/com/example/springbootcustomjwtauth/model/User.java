package com.example.springbootcustomjwtauth.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Set;

@Getter
@Setter
@Document("User")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class User extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private Set<RoleType> roleType;
}
