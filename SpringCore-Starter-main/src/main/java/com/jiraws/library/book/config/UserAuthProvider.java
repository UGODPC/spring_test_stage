package com.jiraws.library.book.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jiraws.library.book.dto.UserDTO;
import com.jiraws.library.book.exceptions.AppException;
import com.jiraws.library.book.mappers.UserMapper;
import com.jiraws.library.book.model.UserEntity;
import com.jiraws.library.book.model.Permission;
import com.jiraws.library.book.model.Role;
import com.jiraws.library.book.persistence.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(UserDTO.PostInput userDTO)
    {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3_600_000);

        UserEntity user = userRepository.findByLogin(userDTO.getLogin()).orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));

        //CONVERTIR LES SETS EN LISTES POUR LE JWT
        List<String> roles = user.getRoles() != null //Liste des rôles non nulle
                ? user.getRoles().stream().map(Role::name).collect(Collectors.toList())
                : List.of();
                //Si la liste n'est pas nulle,
                //stream() créé un nouveau résultat basé sur la liste des rôles et on peut l'utiliser qu'une fois
                //map() transforme chaques rôles en string en appelant leur nom (::name) car Role est un Enum
                //collect() collecte la map pour créer une nouvelle List
                //Si la liste est nulle, on renvoie une liste qui n'est jamais vide car List.of() ne l'accèpte pas

        List<String> permissions = user.getPermissions() != null
                ? user.getPermissions().stream().map(Permission::name).collect(Collectors.toList())
                : List.of();
                //Pareil que pour les rôles

        return JWT.create()
                .withIssuer(userDTO.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("firstName", userDTO.getFirstName())
                .withClaim("lastName", userDTO.getLastName())
                .withClaim("roles", roles)
                .withClaim("permissions", permissions)
                .sign(Algorithm.HMAC256(secretKey));

    }

    public Authentication valideToken(String token)
    {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        //EXTRAIRE LES RÔLES ET PERMISSIONS DU JWT
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        List<String> permissions = decodedJWT.getClaim("permissions").asList(String.class);

        UserDTO.PostInput userDTO = UserDTO.PostInput.builder()
                .login(decodedJWT.getIssuer())
                .firstName(decodedJWT.getClaim("firstName").asString())
                .lastName(decodedJWT.getClaim("lastName").asString())
                .build();

        //CONSTRUIRE LES AUTORITÉES
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if(roles != null) {
            roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        //Si la liste des rôles extraits du JWT n'est pas nulle
        //On créé un stream qui va mapper chaques rôles en de SimpleGrantedAuthority (objet) et qui va ajouter tous ces derniers à la liste authorities

        if(permissions != null) {
            permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        //Pareil qu'au dessus pour les rôles

        //PERMISSION PAR DÉFAUT SI AUCUNE AUTORITÉ
        if(authorities.isEmpty())
        {
            authorities.add(new SimpleGrantedAuthority("BOOK_READ"));
        }

        return new UsernamePasswordAuthenticationToken(userDTO, null, authorities); //Au lieu de la liste vide, on utilise authorities
    }

    public Authentication valideTokenStrongly(String token)
    {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        UserEntity user = userRepository.findByLogin(decodedJWT.getIssuer()).orElseThrow(() -> new AppException("Utilisateur inconnu", HttpStatus.NOT_FOUND));

        //CHARGER TOUTES LES AUTORITÉS DEPUIS LA BASE
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.getRoles() != null) {
            user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.name())) //Pour plus de sécurité, chaques objets seont créé sur le nom des rôles
                    .forEach(authorities::add);
        }

        if (user.getPermissions() != null) {
            user.getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.name()))
                    .forEach(authorities::add);
        }

        UserDTO.PostInput userDTO = userMapper.toUserDTO(user);

        return new UsernamePasswordAuthenticationToken(userDTO, null, authorities);
    }
}
