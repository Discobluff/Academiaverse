package com.academiaverse.controller;

import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.PointsService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class PointsController {
    private final PointsService pointsService;
    private final UserRepository userRepository;

    @PostMapping("/{id}/buy-with-points")
    public ResponseEntity<Map<String, Integer>> buyWithPoints(@PathVariable Long id,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        User buyer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Map<String, Integer> result = pointsService.buyWithPoints(buyer, id);
        return ResponseEntity.ok(result);
    }
}