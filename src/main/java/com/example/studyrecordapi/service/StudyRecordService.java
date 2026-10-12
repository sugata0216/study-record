package com.example.studyrecordapi.service;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.exception.StudyRecordArgumentNotValidException;
import com.example.studyrecordapi.exception.StudyRecordNotFoundException;
import com.example.studyrecordapi.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        if (studyRecord.getStudyDate() == null) {
            throw new StudyRecordArgumentNotValidException("studyDate is empty");
        }
        if (studyRecord.getSubject() == null || studyRecord.getSubject().isEmpty()) {
            throw new StudyRecordArgumentNotValidException("subject is empty");
        }
        if (studyRecord.getTopic() == null || studyRecord.getTopic().isEmpty()) {
            throw new StudyRecordArgumentNotValidException("topic is empty");
        }
        if (studyRecord.getMinutes() == null) {
            throw new StudyRecordArgumentNotValidException("minutes is empty");
        }
        studyRecordRepository.insert(studyRecord);
        return studyRecord;
    }
    @Transactional
    public StudyRecord update(StudyRecord studyRecord, long id) {
        if (studyRecord.getStudyDate() == null) {
            throw new StudyRecordArgumentNotValidException("studyDate is empty");
        }
        if (studyRecord.getSubject() == null || studyRecord.getSubject().isEmpty()) {
            throw new StudyRecordArgumentNotValidException("subject is empty");
        }
        if (studyRecord.getTopic() == null || studyRecord.getTopic().isEmpty()) {
            throw new StudyRecordArgumentNotValidException("topic is empty");
        }
        if (studyRecord.getMinutes() == null) {
            throw new StudyRecordArgumentNotValidException("minutes is empty");
        }
        StudyRecord targetStudyRecord = studyRecordRepository.findById(id);
        if (targetStudyRecord == null) {
            throw new StudyRecordNotFoundException(id);
        }
        studyRecord.setId(id);
        return studyRecordRepository.update(studyRecord);
    }
    public boolean delete(long id) {
        if (!studyRecordRepository.delete(id)) {
            throw new StudyRecordNotFoundException(id);
        }
        return true;
    }
    public List<StudyRecord> search(String subject, LocalDate studyDate) {
        String normalizedSubject = (subject == null || subject.isBlank()) ? null : subject.trim();
        return studyRecordRepository.search(normalizedSubject, studyDate);
    }
}
