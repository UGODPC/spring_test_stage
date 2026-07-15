package com.jiraws.library.book.controllers;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class MessagesController {
    @GetMapping("messages")
    public ResponseEntity<List<String>> messages()
    {
        return ResponseEntity.ok(Arrays.asList("1er", "2e"));
    }
}