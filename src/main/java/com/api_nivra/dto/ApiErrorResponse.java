package com.api_nivra.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiErrorResponse {
    

    private int status;
    private String error;
    private String message;
    private List<FieldErrorDTO> details;
    private String path;
    private LocalDateTime timestamp;
    private String debugMessage; 
}
