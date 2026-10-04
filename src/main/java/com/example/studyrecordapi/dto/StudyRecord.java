package com.example.studyrecordapi.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudyRecord {
    private long id;
    private LocalDate studyDate;
    private String subject;
    private String topic;
    private Integer minutes;
    private String memo;
}
