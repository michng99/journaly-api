package com.journaly.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntryResponse {
    private UUID entryId;
    private String guessPhrase;
    private List<String> suggestedTags;

    private boolean triggerInsightNudge;
}