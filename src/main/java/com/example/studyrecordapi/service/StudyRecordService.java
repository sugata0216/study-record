package com.example.studyrecordapi.service;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.dto.StudyRecordPageResponse;
import com.example.studyrecordapi.exception.StudyRecordArgumentNotValidException;
import com.example.studyrecordapi.exception.StudyRecordNotFoundException;
import com.example.studyrecordapi.repository.StudyRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudyRecordService {
    private final StudyRecordRepository studyRecordRepository;

    public StudyRecordService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }
    public StudyRecordPageResponse findAll(int page, int size) {
        return createPage(null, null, page, size);
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
    public StudyRecordPageResponse search(String subject, LocalDate studyDate, int page, int size) {
        String normalizedSubject = (subject == null || subject.isBlank()) ? null : subject.trim();
        return createPage(normalizedSubject, studyDate, page, size);
    }
    public long findTotalStudyMinutes(String subject, LocalDate studyDate) {
        String normalizedSubject = (subject == null || subject.isBlank()) ? null : subject.trim();
        return studyRecordRepository.sumTotalMinutes(normalizedSubject, studyDate);
    }
    private StudyRecordPageResponse createPage(String subject, LocalDate studyDate, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new StudyRecordArgumentNotValidException("page or size is invalid");
        }
        long offset = (long) page * size;
        List<StudyRecord> records = studyRecordRepository.search(subject, studyDate, size, offset);
        long totalElements = studyRecordRepository.count(subject, studyDate);
        long totalPages = totalElements / size + (totalElements % size == 0 ? 0 : 1);
        return new StudyRecordPageResponse(records, page, size, totalElements, totalPages);
    }
}
