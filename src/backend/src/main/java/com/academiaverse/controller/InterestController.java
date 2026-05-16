package com.academiaverse.controller;

import com.academiaverse.entity.Interest;
import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.InterestService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Interest>> getInterests(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Interest> interests = interestService.getInterestsForUser(user);
        return ResponseEntity.ok(interests);
    }

    @PostMapping
    public ResponseEntity<Interest> createInterest(@RequestBody Map<String, Long> body,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        User buyer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Long itemId = body.get("itemId");
        Interest interest = interestService.createInterest(buyer, itemId);
        return ResponseEntity.ok(interest);
    }
}