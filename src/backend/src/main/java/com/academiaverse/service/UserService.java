package com.academiaverse.service;

import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Integer id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }
        log.info("User with id: {} doesn't exist", id);
        return null;
    }

    public User saveUser (User user){
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        log.info("User with id: {} saved successfully", user.getId());
        return savedUser;
    }

    public User updateUser (User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        user.setCreatedAt(existingUser.get().getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        log.info("User with id: {} updated successfully", user.getId());
        return updatedUser;
    }

    public void deleteUserById (Integer id) {
        userRepository.deleteById(id);
    }

}