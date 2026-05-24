package com.academiaverse.repository;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    void deleteByItem(Item item);
}