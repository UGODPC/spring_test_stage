package com.jiraws.library.book.mappers;

import com.jiraws.library.book.dto.SignUpDTO;
import com.jiraws.library.book.dto.UserDTO;
import com.jiraws.library.book.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true) // Le token sera généré plus tard
    UserDTO.PostInput toUserDTO(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity signUpToUser(SignUpDTO signUpDTO);
}