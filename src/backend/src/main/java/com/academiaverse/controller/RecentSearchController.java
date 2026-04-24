package com.academiaverse.controller;

import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.RecentSearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class RecentSearchController {
    private final RecentSearchService recentSearchService;
    private final UserRepository userRepository;

    @GetMapping("/recent")
    public ResponseEntity<List<String>> getRecentSearches(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(recentSearchService.getRecentSearches(user));
    }

    @PostMapping("/recent")
    public ResponseEntity<Void> saveRecentSearch(@RequestParam String query, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        recentSearchService.saveSearch(user, query);
        return ResponseEntity.ok().build();
    }
}
