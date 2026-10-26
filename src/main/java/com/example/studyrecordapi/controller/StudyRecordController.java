package com.example.studyrecordapi.controller;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.dto.StudyRecordPageResponse;
import com.example.studyrecordapi.dto.StudyTimeSummary;
import com.example.studyrecordapi.service.StudyRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {
    private final StudyRecordService studyRecordService;

    public StudyRecordController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }
    // 学習記録をページ単位で全件取得する
    @GetMapping
    public ResponseEntity<StudyRecordPageResponse> findAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        validatePagination(page, size);
        return ResponseEntity.ok(studyRecordService.findAll(page, size));
    }
    // URLに指定されたIDの学習記録を1件取得する
    @GetMapping("/{id}")
    public ResponseEntity<StudyRecord> findByRecordById(@PathVariable long id) {
        StudyRecord studyRecord = studyRecordService.findById(id);
        return ResponseEntity.ok(studyRecord);
    }
    // リクエストボディの学習記録を登録し、作成したリソースのURLをLocationヘッダーに付ける
    @PostMapping
    public ResponseEntity<StudyRecord> saveRecord(
            @RequestBody StudyRecord studyRecord,
            UriComponentsBuilder uriBuilder) {
        StudyRecord createdRecord = studyRecordService.save(studyRecord);
        URI location = uriBuilder.path("/api/study-records/{id}")
                .buildAndExpand(createdRecord.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdRecord);
    }
    // URLに指定されたIDの学習記録をリクエストボディの内容で更新する
    @PutMapping("/{id}")
    public ResponseEntity<StudyRecord> updateRecord(@PathVariable long id,
                                                    @RequestBody StudyRecord studyRecord) {
        StudyRecord updatedRecord = studyRecordService.update(studyRecord, id);
        return ResponseEntity.ok(updatedRecord);
    }
    // URLに指定されたIDの学習記録を削除し、成功時は本文なしの204を返す
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable long id) {
        studyRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // 科目(部分一致)や学習日で絞り込み、結果をページ単位で取得する
    @GetMapping("/search")
    public ResponseEntity<StudyRecordPageResponse> searchRecords(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        validatePagination(page, size);
        return ResponseEntity.ok(studyRecordService.search(subject, studyDate, page, size));
    }
    // 登録済みの学習記録に含まれる学習時間を合計して返す
    @GetMapping("/total-minutes")
    public ResponseEntity<StudyTimeSummary> findTotalStudyMinutes(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate) {
        StudyTimeSummary summary = new StudyTimeSummary(studyRecordService.findTotalStudyMinutes(subject, studyDate));
        return ResponseEntity.ok(summary);
    }
    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ページは0以上、sizeは1から100の範囲で指定してください");
        }
    }
}