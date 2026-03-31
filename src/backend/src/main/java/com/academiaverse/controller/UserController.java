package com.academiaverse.controller;

import com.academiaverse.entity.User;
import com.academiaverse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers(){
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id)
    {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user)
    {
        return ResponseEntity.ok().body(userService.saveUser(user));
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user)
    {
        return ResponseEntity.ok().body(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer id)
    {
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("Deleted user successfully");
    }
}