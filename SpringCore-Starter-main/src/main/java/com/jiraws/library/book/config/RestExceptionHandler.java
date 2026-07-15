package com.jiraws.library.book.config;

import com.jiraws.library.book.dto.ErrorDTO;
import com.jiraws.library.book.exceptions.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {AppException.class})
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleException(AppException ex)
    {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorDTO(ex.getMessage()));
    }
}
