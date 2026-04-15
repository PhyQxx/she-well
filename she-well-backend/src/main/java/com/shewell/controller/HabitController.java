package com.shewell.controller;

import com.shewell.entity.Habit;
import com.shewell.service.HabitService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @GetMapping("/habit/list")
    public Result<List<Habit>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(habitService.lambdaQuery()
            .eq(Habit::getUserId, userId)
            .orderByDesc(Habit::getCreateTime).list());
    }

    @PostMapping("/habit")
    public Result<Habit> create(@RequestBody Habit habit, HttpServletRequest request) {
        habit.setUserId((Long) request.getAttribute("userId"));
        habitService.save(habit);
        return Result.ok(habit);
    }

    @PutMapping("/habit/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Habit habit, HttpServletRequest request) {
        habit.setId(id);
        habit.setUserId((Long) request.getAttribute("userId"));
        habitService.updateById(habit);
        return Result.ok();
    }

    @DeleteMapping("/habit/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        habitService.lambdaUpdate()
            .eq(Habit::getId, id)
            .eq(Habit::getUserId, request.getAttribute("userId"))
            .remove();
        return Result.ok();
    }
}
