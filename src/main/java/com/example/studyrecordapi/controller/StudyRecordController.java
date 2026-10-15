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
    @GetMapping
    public ResponseEntity<StudyRecordPageResponse> findAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        validatePagination(page, size);
        return ResponseEntity.ok(studyRecordService.findAll(page, size));
    }
    @GetMapping("/{id}")
    public ResponseEntity<StudyRecord> findByRecordById(@PathVariable long id) {
        StudyRecord studyRecord = studyRecordService.findById(id);
        return ResponseEntity.ok(studyRecord);
    }
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
    @PutMapping("/{id}")
    public ResponseEntity<StudyRecord> updateRecord(@PathVariable long id,
                                                    @RequestBody StudyRecord studyRecord) {
        StudyRecord updatedRecord = studyRecordService.update(studyRecord, id);
        return ResponseEntity.ok(updatedRecord);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable long id) {
        studyRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
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
    @GetMapping("/total-minutes")
    public ResponseEntity<StudyTimeSummary> findTotalStudyMinutes() {
        StudyTimeSummary summary = new StudyTimeSummary(studyRecordService.findTotalStudyMinutes());
        return ResponseEntity.ok(summary);
    }
    private void validatePagination(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ページは0以上、sizeは1から100の範囲で指定してください");
        }
    }
}
