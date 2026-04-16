package com.academiaverse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academiaverse.entity.User;

@RequestMapping("/v1/test")
@RestController
public class Test {

    @GetMapping
    public ResponseEntity<String> getTest(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return  ResponseEntity.ok().body("Hello:  " + user.getEmail());
    }
}
