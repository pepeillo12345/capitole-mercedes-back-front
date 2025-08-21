package com.crazy.back.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime timestamp;

    private List<String> details;
}
