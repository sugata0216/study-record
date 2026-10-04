package com.example.studyrecordapi.controller;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.service.StudyRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
}
