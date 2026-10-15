package com.example.studyrecordapi.mapper;

import com.example.studyrecordapi.dto.StudyRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
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
    @Delete("DELETE FROM study_records WHERE id = #{id}")
    int delete(long id);
    @Update("UPDATE study_records SET study_date = #{studyDate}, subject = #{subject}, topic = #{topic}, minutes = #{minutes}, memo = #{memo} WHERE id = #{id}")
    int update(StudyRecord studyRecord);
    @Select("""
        <script>
            SELECT id, study_date AS "studyDate", subject, topic, minutes, memo
            FROM study_records
            <where>
                 <if test='subject != null and subject != ""'>
                    AND LOWER(subject) LIKE LOWER(CONCAT('%', #{subject}, '%'))
                 </if>
                 <if test='studyDate != null'>
                    AND study_date = #{studyDate}
                </if>
            </where>
            ORDER BY study_date DESC, id DESC
            LIMIT #{limit} OFFSET #{offset}
        </script>
    """)
    List<StudyRecord> search(String subject, LocalDate studyDate, int limit, long offset);
    @Select("""
        SELECT COALESCE(SUM(minutes), 0)
        FROM study_records
    """)
    long sumTotalMinutes();
    @Select("""
        <script>
            SELECT COUNT(*)
            FROM study_records
            <where>
                 <if test='subject != null and subject != ""'>
                    AND LOWER(subject) LIKE LOWER(CONCAT('%', #{subject}, '%'))
                 </if>
                 <if test='studyDate != null'>
                    AND study_date = #{studyDate}
                </if>
            </where>
        </script>
    """)
    long count(String subject, LocalDate studyDate);
}
