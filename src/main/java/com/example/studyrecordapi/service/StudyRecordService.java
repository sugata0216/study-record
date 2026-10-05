package com.example.studyrecordapi.service;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.exception.StudyRecordNotFoundException;
import com.example.studyrecordapi.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyRecordService {
    private final StudyRecordRepository studyRecordRepository;

    public StudyRecordService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }
    public List<StudyRecord> findAll() {
        return studyRecordRepository.findAll();
    }
    public StudyRecord findById(long id) {
        StudyRecord record = studyRecordRepository.findById(id);
        if (record == null) {
            throw new StudyRecordNotFoundException(id);
        }
        return record;
    }
    public StudyRecord save(StudyRecord studyRecord) {
        studyRecordRepository.insert(studyRecord);
        return studyRecord;
    }
}
