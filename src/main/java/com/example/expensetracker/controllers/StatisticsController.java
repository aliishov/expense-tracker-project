package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.statistics.DetailedStatisticResponseDto;
import com.example.expensetracker.dtos.statistics.StatisticResponseDto;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.services.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Statistics Controller",
        description = "Manage Transactions Statistics in Project")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public ResponseEntity<StatisticResponseDto> getSummary(@AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return statisticsService.getSummary(userId);
    }

    @GetMapping("/detailed-summary")
    public ResponseEntity<DetailedStatisticResponseDto> getDetailedSummary(@RequestParam(name = "startDate") LocalDateTime startDate,
                                                                           @RequestParam(name = "endDate") LocalDateTime endDate,
                                                                           @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return statisticsService.getDetailedSummary(startDate, endDate, userId);
    }
}
