package com.shewell.controller;

import com.shewell.entity.Activity;
import com.shewell.service.ActivityService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/list")
    public Result<List<Activity>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(activityService.lambdaQuery()
            .eq(Activity::getStatus, 1)
            .orderByDesc(Activity::getCreateTime)
            .last("LIMIT " + (page - 1) * size + "," + size).list());
    }

    @GetMapping("/{id}")
    public Result<Activity> get(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return activity != null ? Result.ok(activity) : Result.fail("活动不存在");
    }

    // ===== 管理端活动 CRUD =====
    @PostMapping
    public Result<Activity> create(@RequestBody Activity activity) {
        activity.setCreateTime(java.time.LocalDateTime.now());
        activity.setStatus("active");
        if (activity.getParticipantCount() == null) activity.setParticipantCount(0);
        activityService.save(activity);
        return Result.ok(activity);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activityService.updateById(activity);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.removeById(id);
        return Result.ok();
    }
}
