package com.jiraws.library.book.controllers;

import com.jiraws.library.book.model.UserEntity;
import com.jiraws.library.book.persistence.UserRepository;
import com.jiraws.library.book.service.UserService;
import org.apache.catalina.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController
{
    private final UserService userService;
    private final UserRepository userRepository;

    public  UserController(UserService userService, UserRepository userRepository)
    {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserEntity> get()
    {
        List<UserEntity> listeUsers = userService.getAllUsers();

        return(listeUsers);
    }
}
