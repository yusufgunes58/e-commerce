package com.example.ecommerce.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

// Error Schema
// {
//           "timestamp": "2024-09-09T10:00:00",
//           "status": 404,
//            "code": "PRODUCT_NOT_FOUND",
//           "message": "Ürün bulunamadı",
//            "path": "/api/v1/products/15"
// }

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // success: its not included.
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String code;
    private final String message;
    private final String path;

    private final Map<String, String> errors;

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.name())
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse ofValidation(String path, Map<String, String> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ErrorCode.VALIDATION_ERROR.getHttpStatus().value())
                .code(ErrorCode.VALIDATION_ERROR.name())
                .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage())
                .path(path)
                .errors(errors)
                .build();
    }


}
