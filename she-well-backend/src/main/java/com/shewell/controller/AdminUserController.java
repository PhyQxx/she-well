package com.shewell.controller;

import com.shewell.entity.User;
import com.shewell.entity.UserProfile;
import com.shewell.service.UserService;
import com.shewell.service.UserProfileService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mode) {
        var query = userService.lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            query.like(User::getNickname, keyword).or().like(User::getPhone, keyword);
        }
        if (status != null && !status.isBlank()) {
            query.eq(User::getStatus, Integer.parseInt(status));
        }
        if (mode != null && !mode.isBlank()) {
            query.eq(User::getCurrentMode, mode);
        }
        long total = query.count();
        List<User> records = query
            .orderByDesc(User::getCreateTime)
            .last("LIMIT " + (page - 1) * pageSize + ", " + pageSize)
            .list();
        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : records) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("nickname", u.getNickname());
            m.put("phone", u.getPhone());
            m.put("avatar", u.getAvatar());
            m.put("currentMode", u.getCurrentMode());
            m.put("status", u.getStatus());
            m.put("createTime", u.getCreateTime());
            list.add(m);
        }
        return Result.ok(Map.of("list", list, "total", total, "page", page, "pageSize", pageSize));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userService.getById(id);
        if (user == null) return Result.fail("用户不存在");
        user.setStatus(body.get("status"));
        userService.updateById(user);
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // 演示：重置密码为123456
        return Result.ok();
    }

    @GetMapping("/{id}/detail")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) return Result.fail("用户不存在");
        UserProfile profile = userProfileService.lambdaQuery()
            .eq(UserProfile::getUserId, id).one();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("user", user);
        result.put("profile", profile);
        return Result.ok(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 软删除
        User user = userService.getById(id);
        if (user == null) return Result.fail("用户不存在");
        userService.lambdaUpdate().eq(User::getId, id).set(User::getStatus, -1).update();
        return Result.ok();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        long total = userService.count();
        long today = userService.lambdaQuery()
            .ge(User::getCreateTime, LocalDateTime.now().toLocalDate().toString() + " 00:00:00")
            .count();
        long active = userService.lambdaQuery().eq(User::getStatus, 1).count();
        long disabled = userService.lambdaQuery().eq(User::getStatus, 0).count();
        return Result.ok(Map.of("total", total, "today", today, "active", active, "disabled", disabled));
    }
}
