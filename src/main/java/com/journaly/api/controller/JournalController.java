package com.journaly.api.controller;

import com.journaly.api.dto.CreateEntryRequest;
import com.journaly.api.dto.CreateEntryResponse;
import com.journaly.api.dto.UpdateTagsRequest;
import com.journaly.api.entity.JournalEntry;
import com.journaly.api.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    /**
     * API Endpoint để tạo một bài nhật ký mới.
     * @param request Đối tượng chứa nội dung bài viết.
     * @return Một đối tượng ResponseEntity chứa thông tin phản hồi (ID, lời phỏng đoán, tag gợi ý).
     */
    @PostMapping("/create")
    public ResponseEntity<CreateEntryResponse> createEntry(@Valid @RequestBody CreateEntryRequest request) {
        log.info("Creating journal entry with content length: {}", request.getContent().length());
        CreateEntryResponse response = journalService.createJournalEntry(request.getContent());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * API Endpoint để cập nhật các tag cho một bài viết đã tồn tại.
     * @param entryId ID của bài viết cần cập nhật (lấy từ đường dẫn URL).
     * @param request Đối tượng chứa danh sách các tên tag.
     * @return Một đối tượng ResponseEntity chứa thông tin bài viết đã được cập nhật.
     */
    @PutMapping("/{entryId}/tags")
    public ResponseEntity<JournalEntry> updateEntryTags(@PathVariable("entryId") UUID entryId, 
                                                        @Valid @RequestBody UpdateTagsRequest request) {
        log.info("Updating tags for entry ID: {}", entryId);
        JournalEntry updatedEntry = journalService.updateTagsForEntry(entryId, request.getTagNames());
        return ResponseEntity.ok(updatedEntry);
    }

    /**
     * Get all journal entries with pagination
     */
    @GetMapping
    public ResponseEntity<Page<JournalEntry>> getAllEntries(Pageable pageable) {
        Page<JournalEntry> entries = journalService.getAllEntries(pageable);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get journal entry by ID
     */
    @GetMapping("/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable UUID entryId) {
        JournalEntry entry = journalService.getEntryById(entryId);
        return ResponseEntity.ok(entry);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Journal API is running");
    }
}