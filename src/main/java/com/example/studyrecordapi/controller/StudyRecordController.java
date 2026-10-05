package com.example.studyrecordapi.controller;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.service.StudyRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {
    private final StudyRecordService studyRecordService;

    public StudyRecordController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }
    @GetMapping
    public ResponseEntity<List<StudyRecord>> findAllRecords() {
        List<StudyRecord> records = studyRecordService.findAll();
        return ResponseEntity.ok(records);
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
}
