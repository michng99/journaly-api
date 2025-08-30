package com.journaly.api.service;

import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.ai.textanalytics.models.TextSentiment;
import com.journaly.api.dto.CreateEntryResponse;
import com.journaly.api.entity.JournalEntry;
import com.journaly.api.entity.Tag;
import com.journaly.api.entity.User;
import com.journaly.api.repository.JournalEntryRepository;
import com.journaly.api.repository.TagRepository;
import com.journaly.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.azure.ai.textanalytics.models.TextSentiment.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalService {

    // Dependencies được inject tự động
    private final JournalEntryRepository journalEntryRepository;
    private final TagRepository tagRepository;
    private final AIService aiService;
    private final UserRepository userRepository;

    // Dùng Map để lưu trữ các câu phỏng đoán, khai báo một lần duy nhất
    private static final Map<TextSentiment, List<String>> GUESS_PHRASES = Map.of(
            POSITIVE, List.of("Có vẻ như đây là một khoảnh khắc đáng nhớ.", "Dường như có một niềm vui nho nhỏ ở đây."),
            NEGATIVE, List.of("Dường như đây là một cảm xúc khá nặng nề.", "Cảm nhận được rằng bạn đang không được vui."),
            MIXED, List.of("Cảm nhận được có nhiều cảm xúc lẫn lộn trong đây.", "Dường như đây là một cảm xúc khá phức tạp."),
            NEUTRAL, List.of("Có vẻ như bạn đang suy tư về một điều gì đó.", "Một khoảnh khắc thật bình yên.")
    );

    // Dùng Map để lưu trữ các tag gợi ý
    private static final Map<TextSentiment, List<String>> SUGGESTED_TAGS = Map.of(
            POSITIVE, List.of("#vui_vẻ", "#biết_ơn", "#hạnh_phúc"),
            NEGATIVE, List.of("#buồn", "#mệt_mỏi", "#tức_giận"),
            MIXED, List.of("#khó_tả", "#vừa_vui_vừa_buồn", "#bối_rối"),
            NEUTRAL, List.of("#suy_tư", "#bình_yên", "#trống_rỗng")
    );

    private static final Random random = new Random();

    @Transactional
    public CreateEntryResponse createJournalEntry(String content) {
        log.info("Received request to create new journal entry.");

        // Validate input
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }

        User currentUser = userRepository.findFirstByOrderByCreatedAtAsc()
                .orElseGet(() -> {
                    log.warn("No users found. Creating a dummy user for development.");
                    User dummyUser = new User();
                    dummyUser.setEmail("dummyuser@example.com");
                    dummyUser.setPasswordHash("temporary_password");
                    return userRepository.save(dummyUser);
                });

        long entryCountBeforeSaving = journalEntryRepository.count();
        log.info("Total entries before save: {}", entryCountBeforeSaving);
        boolean triggerInsightNudge = (entryCountBeforeSaving == 2);

        log.info("Starting sentiment analysis...");
        DocumentSentiment sentimentAnalysisResult = aiService.analyzeSentiment(content);
        SentimentConfidenceScores scores = sentimentAnalysisResult.getConfidenceScores();
        log.info("AI analysis complete. Scores: Positive={}, Negative={}, Neutral={}",
                scores.getPositive(), scores.getNegative(), scores.getNeutral());

        TextSentiment interpretedSentiment = interpretSentimentFromScores(scores);
        log.info("Interpreted sentiment based on custom logic: {}", interpretedSentiment);

        JournalEntry newEntry = new JournalEntry();
        newEntry.setContent(content);
        newEntry.setUser(currentUser);
        newEntry.setSentimentLabel(interpretedSentiment.toString());
        newEntry.setPositiveScore(BigDecimal.valueOf(scores.getPositive()));
        newEntry.setNegativeScore(BigDecimal.valueOf(scores.getNegative()));
        newEntry.setNeutralScore(BigDecimal.valueOf(scores.getNeutral()));

        log.debug("Attempting to save new entry to database: {}", newEntry);
        JournalEntry savedEntry = journalEntryRepository.save(newEntry);
        log.info("Successfully created new journal entry with ID: {}. Total entries now: {}", savedEntry.getId(), journalEntryRepository.count());

        String guessPhrase = selectGuessPhrase(interpretedSentiment);
        List<String> suggestedTags = selectSuggestedTags(interpretedSentiment);

        return new CreateEntryResponse(savedEntry.getId(), guessPhrase, suggestedTags, triggerInsightNudge);
    }



    /**
     * Cập nhật (gắn) các tag cho một bài viết đã tồn tại.
     * @param entryId ID của bài viết.
     * @param tagNames Danh sách tên các tag.
     * @return Bài viết đã được cập nhật với các tag mới.
     */
    @Transactional
    public JournalEntry updateTagsForEntry(UUID entryId, List<String> tagNames) {
        log.info("Updating tags for entry ID: {}", entryId);
        
        // Validate inputs
        if (entryId == null) {
            throw new IllegalArgumentException("Entry ID cannot be null");
        }
        if (tagNames == null) {
            throw new IllegalArgumentException("Tag names cannot be null");
        }

        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found with id: " + entryId));

        entry.getTags().clear(); // Xóa các tag cũ để cập nhật mới

        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                // Tìm xem tag đã tồn tại chưa, nếu chưa thì tạo tag mới
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            log.info("Tag '{}' not found. Creating new one.", tagName);
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                entry.getTags().add(tag);
            }
        }

        log.info("Saving entry with updated tags. Entry ID: {}", entryId);
        return journalEntryRepository.save(entry);
    }


     // --- PHƯƠNG THỨC DIỄN GIẢI ĐÃ SỬA LỖI ---
     private TextSentiment interpretSentimentFromScores(SentimentConfidenceScores scores) {
        double positive = scores.getPositive();
        double negative = scores.getNegative();
        double neutral = scores.getNeutral(); // Lấy cả điểm neutral

        final double SIGNIFICANT_THRESHOLD = 0.25;

        // Quy tắc 1: Nếu cả tích cực và tiêu cực đều đáng kể -> Hỗn hợp
        if (positive >= SIGNIFICANT_THRESHOLD && negative >= SIGNIFICANT_THRESHOLD) {
            return MIXED;
        }

        // Quy tắc 2: Nếu không, chọn cảm xúc có điểm cao nhất
        if (positive > negative && positive > neutral) {
            return POSITIVE;
        } else if (negative > positive && negative > neutral) {
            return NEGATIVE;
        } else {
            return NEUTRAL;
        }
    }


    private String selectGuessPhrase(TextSentiment sentiment) {
        List<String> phrases = GUESS_PHRASES.getOrDefault(sentiment, List.of("Chúng tôi đã ghi nhận cảm xúc của bạn."));
        return phrases.get(random.nextInt(phrases.size()));
    }

    private List<String> selectSuggestedTags(TextSentiment sentiment) {
        return SUGGESTED_TAGS.getOrDefault(sentiment, List.of());
    }

    /**
     * Get all journal entries with pagination
     */
    @Transactional(readOnly = true)
    public Page<JournalEntry> getAllEntries(Pageable pageable) {
        return journalEntryRepository.findAll(pageable);
    }

    /**
     * Get journal entry by ID
     */
    @Transactional(readOnly = true)
    public JournalEntry getEntryById(UUID entryId) {
        return journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Entry not found with id: " + entryId));
    }
}