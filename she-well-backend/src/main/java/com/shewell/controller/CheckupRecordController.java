package com.shewell.controller;

import com.shewell.entity.CheckupRecord;
import com.shewell.service.CheckupRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkup")
@RequiredArgsConstructor
public class CheckupRecordController {

    private final CheckupRecordService checkupService;

    @GetMapping("/list")
    public Result<List<CheckupRecord>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(checkupService.lambdaQuery()
            .eq(CheckupRecord::getUserId, userId)
            .orderByDesc(CheckupRecord::getCheckupDate)
            .list());
    }

    @PostMapping
    public Result<CheckupRecord> create(@RequestBody CheckupRecord record, HttpServletRequest request) {
        record.setUserId((Long) request.getAttribute("userId"));
        checkupService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/upcoming")
    public Result<CheckupRecord> upcoming(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CheckupRecord record = checkupService.lambdaQuery()
            .eq(CheckupRecord::getUserId, userId)
            .ge(CheckupRecord::getCheckupDate, java.time.LocalDate.now().toString())
            .orderByAsc(CheckupRecord::getCheckupDate)
            .one();
        return Result.ok(record);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CheckupRecord record, HttpServletRequest request) {
        record.setId(id);
        record.setUserId((Long) request.getAttribute("userId"));
        checkupService.updateById(record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        checkupService.lambdaUpdate()
            .eq(CheckupRecord::getId, id)
            .eq(CheckupRecord::getUserId, request.getAttribute("userId"))
            .remove();
        return Result.ok();
    }
}
