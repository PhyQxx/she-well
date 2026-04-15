package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired private NotificationService notificationService;

    @GetMapping("/list")
    public Result<List<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        var query = notificationService.lambdaQuery()
            .eq(Notification::getUserId, userId)
            .orderByDesc(Notification::getCreateTime);
        query.last("LIMIT " + (page - 1) * size + "," + size);
        return Result.ok(query.list());
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = notificationService.lambdaQuery()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, 0).count();
        return Result.ok(count);
    }

    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        Notification n = notificationService.getById(id);
        n.setIsRead(1);
        n.setReadTime(java.time.LocalDateTime.now());
        notificationService.updateById(n);
        return Result.ok();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.lambdaUpdate()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsRead, 0)
            .set(Notification::getIsRead, 1)
            .set(Notification::getReadTime, java.time.LocalDateTime.now())
            .update();
        return Result.ok();
    }
}
