package com.academiaverse.service;

import com.academiaverse.entity.Rating;
import com.academiaverse.entity.Item;
import com.academiaverse.entity.User;
import com.academiaverse.repository.RatingRepository;
import com.academiaverse.repository.ItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final ItemRepository itemRepository;

    public Rating getRatingsForUserAndItem(User user, Item item) {
        return ratingRepository.findByUserAndItem(user, item);
    }

    public Double getAverageRatingsForItem(Item item) {
        List<Rating> ratings = ratingRepository.findByItem(item);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }

    @Transactional
    public Rating createRating(User user, Long itemId, int rating) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Rating existing = ratingRepository.findByUserAndItem(user, item);
        if (existing != null) {
            existing.setRatingValue(rating);
            return ratingRepository.save(existing);
        }

        Rating ratingEnt = new Rating();
        ratingEnt.setItem(item);
        ratingEnt.setUser(user);
        ratingEnt.setRatingValue(rating);
        return ratingRepository.save(ratingEnt);
    }
}