package com.shewell.controller;

import com.shewell.entity.IntercourseRecord;
import com.shewell.service.IntercourseRecordService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/intercourse")
@RequiredArgsConstructor
public class IntercourseRecordController {

    private final IntercourseRecordService intercourseService;

    @GetMapping("/list")
    public Result<List<IntercourseRecord>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(intercourseService.lambdaQuery()
            .eq(IntercourseRecord::getUserId, userId)
            .orderByDesc(IntercourseRecord::getIntercourseDate)
            .list());
    }

    @PostMapping
    public Result<IntercourseRecord> create(@RequestBody IntercourseRecord record, HttpServletRequest request) {
        record.setUserId((Long) request.getAttribute("userId"));
        intercourseService.save(record);
        return Result.ok(record);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        intercourseService.lambdaUpdate()
            .eq(IntercourseRecord::getId, id)
            .eq(IntercourseRecord::getUserId, request.getAttribute("userId"))
            .remove();
        return Result.ok();
    }
}
