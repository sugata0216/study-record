package com.example.studyrecordapi.mapper;

import com.example.studyrecordapi.dto.StudyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyRecordMapper {
    @Select("SELECT id, study_date, subject, topic, minutes, memo FROM study_records ORDER BY study_date DESC, id DESC")
    List<StudyRecord> findAll();
}
