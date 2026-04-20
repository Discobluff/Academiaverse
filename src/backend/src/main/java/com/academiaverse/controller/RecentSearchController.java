package com.academiaverse.controller;

import com.academiaverse.entity.User;
import com.academiaverse.service.RecentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class RecentSearchController {
    private final RecentSearchService recentSearchService;

    @GetMapping("/recent")
    public ResponseEntity<List<String>> getRecentSearches(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(recentSearchService.getRecentSearches(user));
    }

    @PostMapping("/recent")
    public ResponseEntity<Void> saveRecentSearch(@RequestParam String query,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        recentSearchService.saveSearch(user, query);
        return ResponseEntity.ok().build();
    }
}
