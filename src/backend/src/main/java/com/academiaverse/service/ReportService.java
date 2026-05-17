package com.academiaverse.service;

import com.academiaverse.entity.Item;
import com.academiaverse.entity.Report;
import com.academiaverse.entity.User;
import com.academiaverse.repository.ItemRepository;
import com.academiaverse.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Report createReport(User reporter, Long itemId, String reason) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Report report = new Report();
        report.setItem(item);
        report.setReporter(reporter);
        report.setReason(reason);
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
}