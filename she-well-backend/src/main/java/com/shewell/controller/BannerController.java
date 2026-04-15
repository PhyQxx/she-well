package com.shewell.controller;

import com.shewell.entity.Banner;
import com.shewell.service.BannerService;
import com.shewell.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BANNER_CACHE_KEY = "banner:list";

    @GetMapping("/list")
    public Result<List<Banner>> list() {
        @SuppressWarnings("unchecked")
        List<Banner> cached = (List<Banner>) redisTemplate.opsForValue().get(BANNER_CACHE_KEY);
        if (cached != null) return Result.ok(cached);

        List<Banner> banners = bannerService.lambdaQuery()
            .eq(Banner::getStatus, 1)
            .orderByAsc(Banner::getSortOrder)
            .list();
        redisTemplate.opsForValue().set(BANNER_CACHE_KEY, banners, 5, TimeUnit.MINUTES);
        return Result.ok(banners);
    }

    // 运营后台用（实际应在 admin 模块，这里提供基础 CRUD）
    @PostMapping
    public Result<Banner> create(@RequestBody Banner banner) {
        bannerService.save(banner);
        redisTemplate.delete(BANNER_CACHE_KEY);
        return Result.ok(banner);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        bannerService.updateById(banner);
        redisTemplate.delete(BANNER_CACHE_KEY);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        redisTemplate.delete(BANNER_CACHE_KEY);
        return Result.ok();
    }
}
