package com.journaly.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEntryRequest {
    @NotBlank(message = "Content cannot be empty")
    @Size(min = 10, max = 10000, message = "Content must be between 10 and 10000 characters")
    private String content;
}