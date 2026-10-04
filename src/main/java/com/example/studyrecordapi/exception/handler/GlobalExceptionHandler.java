package com.example.studyrecordapi.exception.handler;

import com.example.studyrecordapi.exception.StudyRecordArgumentNotValidException;
import com.example.studyrecordapi.exception.StudyRecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StudyRecordNotFoundException.class)
    public ResponseEntity<String> handleStudyRecordNotFoundException(StudyRecordNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(StudyRecordArgumentNotValidException.class)
    public ResponseEntity<String> handleStudyRecordArgumentNotValidException(StudyRecordArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
