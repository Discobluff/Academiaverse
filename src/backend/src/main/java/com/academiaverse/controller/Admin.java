package com.academiaverse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/admin")
@RestController
public class Admin {

    @GetMapping
    public ResponseEntity<String> getTest(){
        return ResponseEntity.ok().body("admin");
    }
}
