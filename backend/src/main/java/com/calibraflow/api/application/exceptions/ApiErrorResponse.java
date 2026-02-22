package com.calibraflow.api.application.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        OffsetDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {}