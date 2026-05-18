package com.academiaverse.service;

import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        log.info("User with id: {} doesn't exist", id);
        return null;
    }

    public User saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException(null);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPoints(10);              // <-- new user gets 10 points
        User savedUser = userRepository.save(user);

        if (log.isDebugEnabled()) {
            log.debug("User with id: {} saved successfully", user.getId());
        }

        return savedUser;
    }

    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        user.setCreatedAt(existingUser.get().getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        if (log.isDebugEnabled()) {
            log.debug("User with id: {} updated successfully", user.getId());
        }

        return updatedUser;
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}