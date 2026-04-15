package com.shewell.controller;

import com.shewell.entity.*;
import com.shewell.service.*;
import com.shewell.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/api/pregnancy")
public class PregnancyController {

    @Autowired private PregnancyRecordService pregnancyRecordService;
    @Autowired private CheckupRecordService checkupRecordService;
    @Autowired private PregnancyDiaryService pregnancyDiaryService;
    @Autowired private BabyService babyService;

    // ===== 怀孕记录 =====
    @PostMapping("/record")
    public Result<PregnancyRecord> addRecord(@RequestBody PregnancyRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        record.setPregnancyStatus("confirmed");
        if (record.getLastPeriodStart() != null && record.getDueDate() == null) {
            LocalDate due = record.getLastPeriodStart().plusDays(280);
            record.setDueDate(due);
        }
        pregnancyRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/record")
    public Result<PregnancyRecord> getRecord(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PregnancyRecord record = pregnancyRecordService.lambdaQuery()
            .eq(PregnancyRecord::getUserId, userId)
            .eq(PregnancyRecord::getPregnancyStatus, "confirmed").one();
        if (record != null && record.getLastPeriodStart() != null) {
            int week = (int) (LocalDate.now().toEpochDay() - record.getLastPeriodStart().toEpochDay()) / 7;
            int day = (int) ((LocalDate.now().toEpochDay() - record.getLastPeriodStart().toEpochDay()) % 7);
            record.setCurrentWeek(week);
            record.setCurrentDay(day);
        }
        return Result.ok(record);
    }

    @PostMapping("/baby-born")
    public Result<Baby> babyBorn(@RequestBody BabyDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        // 创建宝宝记录
        Baby baby = new Baby();
        baby.setUserId(userId);
        baby.setName(dto.getName());
        baby.setBirthDate(dto.getBirthDate());
        baby.setGender(dto.getGender() != null ? dto.getGender() : "unknown");
        baby.setDeliveryType(dto.getDeliveryType() != null ? dto.getDeliveryType() : "unknown");
        baby.setBirthOrder(1);
        baby.setIsPrimary(1);
        babyService.save(baby);
        // 回填pregnancy_record
        PregnancyRecord record = pregnancyRecordService.lambdaQuery()
            .eq(PregnancyRecord::getUserId, userId)
            .eq(PregnancyRecord::getPregnancyStatus, "confirmed").one();
        if (record != null) {
            record.setBabyId(baby.getId());
            record.setEndDate(dto.getBirthDate());
            record.setEndReason(dto.getDeliveryType() != null ? dto.getDeliveryType() : "顺产");
            record.setPregnancyStatus("ended");
            pregnancyRecordService.updateById(record);
        }
        return Result.ok(baby);
    }

    // ===== 产检记录 =====
    @PostMapping("/checkup")
    public Result<CheckupRecord> addCheckup(@RequestBody CheckupRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        checkupRecordService.save(record);
        return Result.ok(record);
    }

    @GetMapping("/checkup/list")
    public Result<List<CheckupRecord>> getCheckups(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CheckupRecord> records = checkupRecordService.lambdaQuery()
            .eq(CheckupRecord::getUserId, userId)
            .orderByDesc(CheckupRecord::getCheckupDate).list();
        return Result.ok(records);
    }

    // ===== 孕期日记 =====
    @PostMapping("/diary")
    public Result<PregnancyDiary> addDiary(@RequestBody PregnancyDiary diary, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        diary.setUserId(userId);
        pregnancyDiaryService.save(diary);
        return Result.ok(diary);
    }

    @GetMapping("/diary/list")
    public Result<List<PregnancyDiary>> getDiaries(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PregnancyDiary> diaries = pregnancyDiaryService.lambdaQuery()
            .eq(PregnancyDiary::getUserId, userId)
            .orderByDesc(PregnancyDiary::getRecordDate).list();
        return Result.ok(diaries);
    }

    @Data public static class BabyDTO {
        private String name;
        private LocalDate birthDate;
        private String gender;
        private String deliveryType;
    }
}
