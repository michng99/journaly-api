package com.journaly.api.controller;

import com.journaly.api.dto.CreateEntryRequest;
import com.journaly.api.dto.CreateEntryResponse;
import com.journaly.api.dto.UpdateTagsRequest;
import com.journaly.api.entity.JournalEntry;
import com.journaly.api.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class JournalController {

    // Tiêm JournalService vào để sử dụng các phương thức xử lý logic.
    private final JournalService journalService;

    /**
     * API Endpoint để tạo một bài nhật ký mới.
     * @param request Đối tượng chứa nội dung bài viết.
     * @return Một đối tượng ResponseEntity chứa thông tin phản hồi (ID, lời phỏng đoán, tag gợi ý).
     */
    @PostMapping("/create")
    public ResponseEntity<CreateEntryResponse> createEntry(@RequestBody CreateEntryRequest request) {
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
    public ResponseEntity<JournalEntry> updateEntryTags(@PathVariable("entryId") UUID entryId, @RequestBody UpdateTagsRequest request) {
        JournalEntry updatedEntry = journalService.updateTagsForEntry(entryId, request.getTagNames());
        return ResponseEntity.ok(updatedEntry);
    }
}