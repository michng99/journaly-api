package com.journaly.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // Check database
        Map<String, Object> database = checkDatabase();
        health.put("database", database);
        
        // Overall status
        boolean allUp = "UP".equals(database.get("status"));
        health.put("status", allUp ? "UP" : "DOWN");
        
        return ResponseEntity.ok(health);
    }

    private Map<String, Object> checkDatabase() {
        Map<String, Object> status = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            status.put("status", "UP");
            status.put("database", connection.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            log.error("Database health check failed", e);
            status.put("status", "DOWN");
            status.put("error", e.getMessage());
        }
        return status;
    }


}
