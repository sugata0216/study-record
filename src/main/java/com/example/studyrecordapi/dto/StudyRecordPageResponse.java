package com.example.studyrecordapi.dto;

import java.util.List;

public record StudyRecordPageResponse(
        List<StudyRecord> content,
        int page,
        int size,
        long totalElements,
        long totalPages
) {
}
