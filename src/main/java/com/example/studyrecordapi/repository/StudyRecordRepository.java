package com.example.studyrecordapi.repository;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.exception.StudyRecordNotFoundException;
import com.example.studyrecordapi.mapper.StudyRecordMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class StudyRecordRepository {
    private final StudyRecordMapper studyRecordMapper;

    public StudyRecordRepository(StudyRecordMapper studyRecordMapper) {
        this.studyRecordMapper = studyRecordMapper;
    }
    public List<StudyRecord> findAll() {
        return studyRecordMapper.findAll();
    }
    public StudyRecord findById(long id) {
        return studyRecordMapper.findById(id);
    }
    public int insert(StudyRecord studyRecord) {
        return studyRecordMapper.insert(studyRecord);
    }
    public StudyRecord update(StudyRecord studyRecord) {
        if (studyRecordMapper.update(studyRecord) == 0) {
            throw new StudyRecordNotFoundException(studyRecord.getId());
        }
        return studyRecord;
    }
    public boolean delete(long id) {
        return studyRecordMapper.delete(id) == 1;
    }
    public List<StudyRecord> search(String subject, LocalDate studyDate, int limit, long offset) {
        return studyRecordMapper.search(subject, studyDate, limit, offset);
    }
    public long sumTotalMinutes() {
        return studyRecordMapper.sumTotalMinutes();
    }
    public long count(String subject, LocalDate studyDate) {
        return studyRecordMapper.count(subject, studyDate);
    }
}
