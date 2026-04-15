package com.shewell.controller;

import com.shewell.entity.CommunityPost;
import com.shewell.entity.CommunityComment;
import com.shewell.entity.PostAuditLog;
import com.shewell.service.CommunityPostService;
import com.shewell.service.CommunityCommentService;
import com.shewell.service.PostAuditLogService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
public class AdminCommunityController {

    private final CommunityPostService postService;
    private final CommunityCommentService commentService;
    private final PostAuditLogService auditLogService;

    // ===== 帖子管理 =====
    @GetMapping("/post/list")
    public Result<?> postList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        var query = postService.lambdaQuery();
        if (status != null && !status.isBlank()) {
            try { query.eq(CommunityPost::getStatus, Integer.parseInt(status)); } catch(Exception e) {}
        }
        if (keyword != null && !keyword.isBlank()) {
            query.like(CommunityPost::getTitle, keyword);
        }
        long total = query.count();
        return Result.ok(Map.of(
            "list", query.orderByDesc(CommunityPost::getCreateTime)
                .last("LIMIT " + (page - 1) * pageSize + ", " + pageSize).list(),
            "total", total
        ));
    }

    @PutMapping("/post/{id}/audit")
    public Result<Void> auditPost(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String action = body.get("action");
        CommunityPost post = postService.getById(id);
        if (post == null) return Result.fail("帖子不存在");
        post.setStatus("approve".equals(action) ? 1 : 2);
        postService.updateById(post);
        // 记录审核日志
        PostAuditLog log = new PostAuditLog();
        log.setPostId(id);
        log.setAuditorId(1L);
        log.setAction(action);
        log.setReason(body.getOrDefault("reason", ""));
        log.setCreateTime(LocalDateTime.now());
        auditLogService.save(log);
        return Result.ok();
    }

    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        CommunityPost post = postService.getById(id);
        if (post != null) {
            post.setStatus(-1);
            postService.updateById(post);
        }
        return Result.ok();
    }

    // ===== 评论管理 =====
    @GetMapping("/comment/list")
    public Result<?> commentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword) {
        var query = commentService.lambdaQuery();
        if (keyword != null && !keyword.isBlank()) {
            query.like(CommunityComment::getContent, keyword);
        }
        return Result.ok(Map.of(
            "list", query.orderByDesc(CommunityComment::getCreateTime)
                .last("LIMIT " + (page - 1) * pageSize + ", " + pageSize).list(),
            "total", query.count()
        ));
    }

    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.ok();
    }
}
