package com.academiaverse.controller;

import com.academiaverse.dto.request.RatingRequest;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.Rating;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.RatingService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingsController {
    private final RatingService ratingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<Double> getAverageRatings(@RequestParam Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return ResponseEntity.ok(ratingService.getAverageRatingsForItem(item));
    }

    @GetMapping("/user")
    public ResponseEntity<Rating> getUserRating(@RequestParam Long itemId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        Rating rating = ratingService.getRatingsForUserAndItem(user, item);
        if (rating == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(rating);
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody RatingRequest request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!item.getBuyers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User has not bought this item");
        }
        if (request.ratingValue() < 1 || request.ratingValue() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        Rating createdRating = ratingService.createRating(user, item.getId(), request.ratingValue());
        return ResponseEntity.ok(createdRating);
    }

}