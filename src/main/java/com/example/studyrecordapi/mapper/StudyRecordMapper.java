package com.example.studyrecordapi.mapper;

import com.example.studyrecordapi.dto.StudyRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyRecordMapper {
    @Select("SELECT id, study_date, subject, topic, minutes, memo FROM study_records ORDER BY study_date DESC, id DESC")
    List<StudyRecord> findAll();
    @Insert("INSERT INTO study_records (study_date, subject, topic, minutes, memo) VALUES (#{studyDate}, #{subject}, #{topic}, #{minutes}, #{memo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudyRecord studyRecord);
    @Select("SELECT id, study_date, subject, topic, minutes, memo FROM study_records WHERE id = #{id}")
    StudyRecord findById(long id);
}
