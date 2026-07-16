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

import java.nio.CharBuffer;
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

    public UserDTO.PostInput register(SignUpDTO signUpDTO)
    {
        Optional<UserEntity> oUser = userRepository.findByLogin(signUpDTO.getLogin());

        if (oUser.isPresent())
        {
            throw new AppException("Le compte existe déjà !", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userMapper.signUpToUser(signUpDTO);
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));

        user.setRoles(Set.of(Role.ROLE_USER)); //Set prend la forme d'une liste sans doublons.
        user.setPermissions(Set.of(
                Permission.BOOK_READ,
                Permission.BOOK_CREATE,
                Permission.BOOK_UPDATE,
                Permission.BOOK_DELETE
        )); //Au moment de la création de compte, l'utilisateur aura le droit de performer les actions si-dessus...

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public UserEntity getUserByLogin(String login)
    {
        return userRepository.findByLogin(login).orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));
    }
}