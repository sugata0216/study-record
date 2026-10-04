package com.example.studyrecordapi.exception;

public class StudyRecordNotFoundException extends RuntimeException {
    public StudyRecordNotFoundException(long id) {
      super("指定されたIDのノートが見つかりません:" + id);
    }
}
