package com.journaly.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateTagsRequest {
    private List<String> tagNames;
}