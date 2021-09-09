package com.nfx.work.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

@RestControllerAdvice
@RequestMapping(produces = "application/json")
public class ErrorControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse>handleHttpExeptions(WebRequest request, RuntimeException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestResponse(e.getMessage()));
    }

    @Getter
    @AllArgsConstructor
    private static class RestResponse<T> {
        private String error;

        public ZonedDateTime getTimesttmp() {
            return ZonedDateTime.now();
        }

    }
}
