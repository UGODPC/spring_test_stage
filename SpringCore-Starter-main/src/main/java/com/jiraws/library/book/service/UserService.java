package com.jiraws.library.book.service;

import com.jiraws.library.book.dto.CredentialsDTO;
import com.jiraws.library.book.dto.SignUpDTO;
import com.jiraws.library.book.dto.UserDTO;
import com.jiraws.library.book.exceptions.AppException;
import com.jiraws.library.book.mappers.UserMapper;
import com.jiraws.library.book.model.Permission;
import com.jiraws.library.book.model.Role;
import com.jiraws.library.book.model.UserEntity;
import com.jiraws.library.book.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService
{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDTO.PostInput login(CredentialsDTO credentialsDTO)
    {
        UserEntity user = userRepository.findByLogin(credentialsDTO.login()).orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches((credentialsDTO.password()), user.getPassword()))
        {
            return userMapper.toUserDTO(user);
        }
        throw new AppException("Mot de passe invalide", HttpStatus.BAD_REQUEST);
    }

    public UserDTO.PostInput register(SignUpDTO signUpDTO) {
        Optional<UserEntity> oUser = userRepository.findByLogin(signUpDTO.getLogin());

        if (oUser.isPresent()) {
            throw new AppException("Le compte existe déjà !", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userMapper.signUpToUser(signUpDTO);
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));

        // ❌ AVANT (problème) :
        // user.setRoles(Set.of(Role.ROLE_GUEST));

        // ✅ APRÈS (corrigé) :
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_GUEST);
        user.setRoles(roles);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.BOOK_READ);
        user.setPermissions(permissions);

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public UserDTO.PostInput promoteGuestToUser(String login) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));

        if (!user.getRoles().contains(Role.ROLE_GUEST)) {
            throw new AppException("L'utilisateur n'est pas un GUEST", HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.BOOK_READ);
        permissions.add(Permission.BOOK_CREATE);
        user.setPermissions(permissions);

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public UserDTO.PostInput promoteUserToAdmin(String login) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));

        if (!user.getRoles().contains(Role.ROLE_USER)) {
            throw new AppException("L'utilisateur n'est pas un USER", HttpStatus.BAD_REQUEST);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_ADMIN);
        user.setRoles(roles);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.BOOK_READ);
        permissions.add(Permission.BOOK_CREATE);
        permissions.add(Permission.BOOK_UPDATE);
        permissions.add(Permission.BOOK_DELETE);
        permissions.add(Permission.USER_READ);
        permissions.add(Permission.USER_CREATE);
        user.setPermissions(permissions);

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public List<UserEntity> getAllUsers()
    {
        return userRepository.findAll();
    }

    public UserEntity getUserByLogin(String login)
    {
        return userRepository.findByLogin(login).orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));
    }
}