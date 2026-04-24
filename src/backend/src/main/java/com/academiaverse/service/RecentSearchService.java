package com.academiaverse.service;

import com.academiaverse.entity.RecentSearch;
import com.academiaverse.entity.User;
import com.academiaverse.repository.RecentSearchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecentSearchService {
    private final RecentSearchRepository recentSearchRepository;

    @Transactional
    public void saveSearch(User user, String query) {
        // Remove duplicate if exists (optional)
        recentSearchRepository.deleteByUserAndQuery(user, query);
        RecentSearch search = new RecentSearch();
        search.setUser(user);
        search.setQuery(query);
        search.setSearchedAt(LocalDateTime.now());
        recentSearchRepository.save(search);
    }

    public List<String> getRecentSearches(User user) {
        return recentSearchRepository.findTop5ByUserOrderBySearchedAtDesc(user)
                .stream()
                .map(RecentSearch::getQuery)
                .collect(Collectors.toList());
    }
}
