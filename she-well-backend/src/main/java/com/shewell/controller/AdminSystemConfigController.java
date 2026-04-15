package com.shewell.controller;

import com.shewell.service.SystemConfigService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public Result<Map<String, String>> getAll() {
        return Result.ok(systemConfigService.getAllConfigs());
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, String> configs) {
        systemConfigService.batchUpdate(configs);
        return Result.ok();
    }
}
