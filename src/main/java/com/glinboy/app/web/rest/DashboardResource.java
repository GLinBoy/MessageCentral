package com.glinboy.app.web.rest;

import com.glinboy.app.domain.MessagesStatistics;
import com.glinboy.app.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing Dashboard.
 */
@RestController
@RequestMapping("/api")
public class DashboardResource {

    public final DashboardService dashboardService;

    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<MessagesStatistics>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getLast30DaysMessagesStatics());
    }
}
