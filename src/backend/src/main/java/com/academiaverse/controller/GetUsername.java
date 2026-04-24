package com.academiaverse.controller;

import com.academiaverse.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// DELETE THIS CLASS IN THE FUTURE, IT IS JUST A TEST TO CHECK THE AUTHENTICATION

@RequestMapping("/v1/test")
@RestController
public class GetUsername {

    @GetMapping
    public ResponseEntity<String> getTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return  ResponseEntity.ok().body(user.getEmail());
    }
}
