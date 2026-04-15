package com.shewell.controller;

import com.shewell.entity.Baby;
import com.shewell.service.BabyService;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/baby")
@RequiredArgsConstructor
public class BabyController {

    private final BabyService babyService;

    @GetMapping("/list")
    public Result<List<Baby>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(babyService.lambdaQuery()
            .eq(Baby::getUserId, userId)
            .orderByDesc(Baby::getBirthDate).list());
    }

    @PostMapping
    public Result<Baby> create(@RequestBody Baby baby, HttpServletRequest request) {
        baby.setUserId((Long) request.getAttribute("userId"));
        babyService.save(baby);
        return Result.ok(baby);
    }

    @GetMapping("/{id}")
    public Result<Baby> get(@PathVariable Long id, HttpServletRequest request) {
        Baby baby = babyService.lambdaQuery()
            .eq(Baby::getId, id)
            .eq(Baby::getUserId, request.getAttribute("userId")).one();
        return baby != null ? Result.ok(baby) : Result.fail("宝宝记录不存在");
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Baby baby, HttpServletRequest request) {
        baby.setId(id);
        baby.setUserId((Long) request.getAttribute("userId"));
        babyService.updateById(baby);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        babyService.lambdaUpdate()
            .eq(Baby::getId, id)
            .eq(Baby::getUserId, request.getAttribute("userId"))
            .remove();
        return Result.ok();
    }
}
