package com.journaly.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class UpdateTagsRequest {
    @NotNull(message = "Tag names cannot be null")
    @Size(min = 1, max = 20, message = "Must have between 1 and 20 tags")
    private List<@NotNull @Size(min = 1, max = 50, message = "Tag name must be between 1 and 50 characters") String> tagNames;
}