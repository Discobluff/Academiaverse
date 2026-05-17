package com.academiaverse.controller;

import com.academiaverse.entity.Report;
import com.academiaverse.entity.User;
import com.academiaverse.repository.UserRepository;
import com.academiaverse.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Map<String, Object> body,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User reporter = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Long itemId = Long.valueOf(body.get("itemId").toString());
        String reason = (String) body.get("reason");
        Report report = reportService.createReport(reporter, itemId, reason);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }
}