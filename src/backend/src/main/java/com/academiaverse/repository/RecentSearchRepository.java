package com.academiaverse.repository;

import com.academiaverse.entity.RecentSearch;
import com.academiaverse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    List<RecentSearch> findTop5ByUserOrderBySearchedAtDesc(User user);
    void deleteByUserAndQuery(User user, String query); // optional, to avoid duplicates
}
