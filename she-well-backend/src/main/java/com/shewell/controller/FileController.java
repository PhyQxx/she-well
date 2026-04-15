package com.shewell.controller;

import com.shewell.util.FtpUtil;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FtpUtil ftpUtil;

    @Value("${ftp.dir.avatar:avatar}")
    private String avatarDir;

    @Value("${ftp.dir.post:post}")
    private String postDir;

    @Value("${ftp.dir.article:article}")
    private String articleDir;

    @Value("${ftp.dir.checkin:checkin}")
    private String checkinDir;

    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return upload(file, avatarDir + "/" + userId, "avatar_" + userId);
    }

    @PostMapping("/upload/post-image")
    public Result<String> uploadPostImage(@RequestParam("file") MultipartFile file) {
        String ext = getExt(file.getOriginalFilename());
        String name = UUID.randomUUID() + ext;
        return upload(file, postDir, name);
    }

    @PostMapping("/upload/checkin")
    public Result<String> uploadCheckinImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String ext = getExt(file.getOriginalFilename());
        String name = "checkin_" + System.currentTimeMillis() + ext;
        return upload(file, checkinDir + "/" + userId + "/" + dateDir, name);
    }

    private Result<String> upload(MultipartFile file, String dir, String fileName) {
        if (file.isEmpty()) return Result.fail("文件不能为空");
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) return Result.fail(413, "文件大小不能超过5MB");
        try {
            byte[] data = file.getBytes();
            String url = ftpUtil.upload(data, dir, fileName);
            return url != null ? Result.ok(url) : Result.fail("上传失败，请稍后再试");
        } catch (IOException e) {
            log.error("文件上传异常: {}", e.getMessage());
            return Result.fail("上传失败：" + e.getMessage());
        }
    }

    private String getExt(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
