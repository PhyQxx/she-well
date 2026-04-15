package com.shewell.controller;

import com.shewell.entity.BbtTempRecord;
import com.shewell.service.BbtTempRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bbt")
@RequiredArgsConstructor
public class BbtTempController {

    private final BbtTempRecordService bbtService;

    @GetMapping("/list")
    public Result<List<BbtTempRecord>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var query = bbtService.lambdaQuery().eq(BbtTempRecord::getUserId, userId);
        if (startDate != null) query.ge(BbtTempRecord::getMeasureDate, startDate);
        if (endDate != null) query.le(BbtTempRecord::getMeasureDate, endDate);
        return Result.ok(query.orderByDesc(BbtTempRecord::getMeasureDate).list());
    }

    @PostMapping
    public Result<BbtTempRecord> create(@RequestBody BbtTempRecord record, HttpServletRequest request) {
        record.setUserId((Long) request.getAttribute("userId"));
        bbtService.save(record);
        return Result.ok(record);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody BbtTempRecord record, HttpServletRequest request) {
        record.setId(id);
        record.setUserId((Long) request.getAttribute("userId"));
        bbtService.updateById(record);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        bbtService.lambdaUpdate()
            .eq(BbtTempRecord::getId, id)
            .eq(BbtTempRecord::getUserId, request.getAttribute("userId"))
            .remove();
        return Result.ok();
    }

    @GetMapping("/latest")
    public Result<BbtTempRecord> latest(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BbtTempRecord record = bbtService.lambdaQuery()
            .eq(BbtTempRecord::getUserId, userId)
            .orderByDesc(BbtTempRecord::getMeasureDate)
            .one();
        return Result.ok(record);
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> trend(
            @RequestParam(defaultValue = "7") int days,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);
        List<BbtTempRecord> records = bbtService.lambdaQuery()
            .eq(BbtTempRecord::getUserId, userId)
            .between(BbtTempRecord::getMeasureDate, start.toString(), end.toString())
            .orderByAsc(BbtTempRecord::getMeasureDate)
            .list();
        return Result.ok(Map.of("records", records, "start", start, "end", end));
    }
}
