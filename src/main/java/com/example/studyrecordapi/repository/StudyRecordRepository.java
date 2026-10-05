package com.example.studyrecordapi.repository;

import com.example.studyrecordapi.dto.StudyRecord;
import com.example.studyrecordapi.mapper.StudyRecordMapper;
import org.springframework.stereotype.Repository;

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

}
