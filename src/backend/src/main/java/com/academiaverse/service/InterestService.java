package com.academiaverse.service;

import com.academiaverse.entity.Interest;
import com.academiaverse.entity.User;
import com.academiaverse.repository.InterestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepository interestRepository;

    public List<Interest> getInterestsForUser(User user) {
        return interestRepository.findByUser(user);
    }
}