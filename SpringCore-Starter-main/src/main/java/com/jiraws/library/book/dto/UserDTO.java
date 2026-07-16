package com.jiraws.library.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostInput {
        private Long id;
        private String firstName;
        private String lastName;
        private String login;
        private String token;
        private List<String> roles;
        private List<String> permissions;
    }
}