package com.shewell.controller;

import com.shewell.entity.CommunityPost;
import com.shewell.entity.CommunityComment;
import com.shewell.entity.CommunityTopic;
import com.shewell.entity.PostLike;
import com.shewell.entity.PostCollect;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityTopicService topicService;
    private final CommunityPostService postService;
    private final CommunityCommentService commentService;
    private final PostLikeService postLikeService;
    private final PostCollectService postCollectService;

    // ===== 话题广场 =====
    @GetMapping("/topics")
    public Result<List<CommunityTopic>> getTopics() {
        return Result.ok(topicService.lambdaQuery()
            .eq(CommunityTopic::getStatus, 1)
            .orderByDesc(CommunityTopic::getSortOrder).list());
    }

    // ===== 姐妹圈帖子 =====
    @PostMapping("/post")
    public Result<CommunityPost> createPost(@RequestBody CommunityPost post, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        post.setUserId(userId);
        post.setStatus(1);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCollectCount(0);
        post.setShareCount(0);
        post.setCreateTime(LocalDateTime.now());
        postService.save(post);
        return Result.ok(post);
    }

    @GetMapping("/posts")
    public Result<?> getPosts(
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        var query = postService.lambdaQuery()
            .eq(topicId != null, CommunityPost::getTopicId, topicId)
            .eq(CommunityPost::getStatus, 1)
            .orderByDesc(CommunityPost::getCreateTime);
        long total = query.count();
        query.last("LIMIT " + (page - 1) * size + "," + size);
        return Result.ok(query.list());
    }

    @GetMapping("/post/{id}")
    public Result<CommunityPost> getPost(@PathVariable Long id) {
        CommunityPost post = postService.getById(id);
        return Result.ok(post);
    }

    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        CommunityPost post = postService.getById(id);
        if (post == null) return Result.fail("帖子不存在");
        if (!post.getUserId().equals(userId)) return Result.fail("无权删除");
        post.setStatus(2);
        postService.updateById(post);
        return Result.ok();
    }

    @PostMapping("/post/{id}/like")
    public Result<Void> likePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostLike existing = postLikeService.lambdaQuery()
            .eq(PostLike::getUserId, userId)
            .eq(PostLike::getTargetType, "post")
            .eq(PostLike::getTargetId, id).one();
        if (existing != null) {
            return Result.fail("已点赞");
        }
        PostLike like = new PostLike();
        like.setUserId(userId);
        like.setTargetType("post");
        like.setTargetId(id);
        like.setCreateTime(LocalDateTime.now());
        postLikeService.save(like);
        postService.lambdaUpdate()
            .setSql("like_count = COALESCE(like_count,0) + 1")
            .eq(CommunityPost::getId, id).update();
        return Result.ok();
    }

    @PostMapping("/post/{id}/collect")
    public Result<Void> collectPost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostCollect collect = new PostCollect();
        collect.setUserId(userId);
        collect.setTargetType("post");
        collect.setTargetId(id);
        collect.setCreateTime(LocalDateTime.now());
        postCollectService.save(collect);
        postService.lambdaUpdate()
            .setSql("collect_count = COALESCE(collect_count,0) + 1")
            .eq(CommunityPost::getId, id).update();
        return Result.ok();
    }

    // ===== 评论 =====
    @PostMapping("/comment")
    public Result<CommunityComment> addComment(@RequestBody CommunityComment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        if (comment.getParentId() == null) comment.setParentId(0L);
        comment.setCreateTime(LocalDateTime.now());
        commentService.save(comment);
        postService.lambdaUpdate()
            .setSql("comment_count = COALESCE(comment_count,0) + 1")
            .eq(CommunityPost::getId, comment.getPostId()).update();
        return Result.ok(comment);
    }

    @GetMapping("/comments")
    public Result<List<CommunityComment>> getComments(@RequestParam Long postId) {
        return Result.ok(commentService.lambdaQuery()
            .eq(CommunityComment::getPostId, postId)
            .eq(CommunityComment::getStatus, 1)
            .orderByAsc(CommunityComment::getCreateTime).list());
    }
}
