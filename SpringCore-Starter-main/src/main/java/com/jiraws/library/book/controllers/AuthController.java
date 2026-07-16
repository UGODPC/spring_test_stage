package com.jiraws.library.book.controllers;

import com.jiraws.library.book.config.UserAuthProvider;
import com.jiraws.library.book.dto.CredentialsDTO;
import com.jiraws.library.book.dto.SignUpDTO;
import com.jiraws.library.book.dto.UserDTO;
import com.jiraws.library.book.model.Permission;
import com.jiraws.library.book.model.Role;
import com.jiraws.library.book.model.UserEntity;
import com.jiraws.library.book.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    public AuthController(UserService userService, UserAuthProvider userAuthProvider) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO.PostInput> login(@RequestBody CredentialsDTO credentialsDTO) {
        // 1. Récupérer les infos de base
        UserDTO.PostInput user = userService.login(credentialsDTO);

        // 2. Récupérer l'utilisateur complet avec ses rôles/permissions
        UserEntity userEntity = userService.getUserByLogin(credentialsDTO.login());

        // 3. Ajouter les rôles et permissions au DTO
        if (userEntity.getRoles() != null) {
            user.setRoles(userEntity.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toList()));
        }

        if (userEntity.getPermissions() != null) {
            user.setPermissions(userEntity.getPermissions().stream()
                    .map(Permission::name)
                    .collect(Collectors.toList()));
        }

        // 4. Générer le token
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO.PostInput> register(@RequestBody SignUpDTO signUpDTO) {
        UserDTO.PostInput user = userService.register(signUpDTO);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }
}