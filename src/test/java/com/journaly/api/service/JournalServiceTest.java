package com.journaly.api.service;

import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.ai.textanalytics.models.TextSentiment;
import com.journaly.api.dto.CreateEntryResponse;
import com.journaly.api.entity.JournalEntry;
import com.journaly.api.entity.User;
import com.journaly.api.repository.JournalEntryRepository;
import com.journaly.api.repository.TagRepository;
import com.journaly.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AIService aiService;

    @InjectMocks
    private JournalService journalService;

    private User testUser;
    private DocumentSentiment testSentiment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");

        SentimentConfidenceScores scores = new SentimentConfidenceScores(0.8, 0.1, 0.1);
        testSentiment = new DocumentSentiment(
                TextSentiment.POSITIVE,
                scores,
                new com.azure.core.util.IterableStream<>(java.util.Collections.emptyList()),
                new com.azure.core.util.IterableStream<>(java.util.Collections.emptyList())
        );
    }

    @Test
    void shouldCreateJournalEntrySuccessfully() {
        // Given
        String content = "Today was a wonderful day!";
        JournalEntry savedEntry = new JournalEntry();
        savedEntry.setId(UUID.randomUUID());
        savedEntry.setContent(content);

        when(userRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(testUser));
        when(journalEntryRepository.count()).thenReturn(1L);
        when(aiService.analyzeSentiment(anyString())).thenReturn(testSentiment);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);

        // When
        CreateEntryResponse response = journalService.createJournalEntry(content);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEntryId()).isEqualTo(savedEntry.getId());
        assertThat(response.getGuessPhrase()).isNotNull();
        assertThat(response.getSuggestedTags()).isNotEmpty();
        assertThat(response.isTriggerInsightNudge()).isFalse();
    }

    @Test
    void shouldThrowExceptionForNullContent() {
        // When & Then
        assertThatThrownBy(() -> journalService.createJournalEntry(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyContent() {
        // When & Then
        assertThatThrownBy(() -> journalService.createJournalEntry("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content cannot be null or empty");
    }

    @Test
    void shouldTriggerInsightNudgeOnThirdEntry() {
        // Given
        String content = "Today was a wonderful day!";
        JournalEntry savedEntry = new JournalEntry();
        savedEntry.setId(UUID.randomUUID());
        savedEntry.setContent(content);

        when(userRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(testUser));
        when(journalEntryRepository.count()).thenReturn(2L); // This will trigger insight nudge
        when(aiService.analyzeSentiment(anyString())).thenReturn(testSentiment);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);

        // When
        CreateEntryResponse response = journalService.createJournalEntry(content);

        // Then
        assertThat(response.isTriggerInsightNudge()).isTrue();
    }
}
