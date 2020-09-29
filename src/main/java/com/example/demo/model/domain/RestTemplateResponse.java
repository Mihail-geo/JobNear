package com.example.demo.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class RestTemplateResponse<T> {
    private T body;
    private HttpStatus status;
    private String error;

    public RestTemplateResponse(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
    }
}
