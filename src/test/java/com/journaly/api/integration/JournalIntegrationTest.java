package com.journaly.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.journaly.api.dto.CreateEntryRequest;
import com.journaly.api.dto.CreateEntryResponse;
import com.journaly.api.entity.JournalEntry;
import com.journaly.api.repository.JournalEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
public class JournalIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @BeforeEach
    void setUp() {
        journalEntryRepository.deleteAll();
    }

    @Test
    void shouldCreateJournalEntry() throws Exception {
        CreateEntryRequest request = new CreateEntryRequest();
        request.setContent("Today was a wonderful day! I feel very happy and grateful.");

        mockMvc.perform(post("/api/entries/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entryId", notNullValue()))
                .andExpect(jsonPath("$.guessPhrase", notNullValue()))
                .andExpect(jsonPath("$.suggestedTags", hasSize(greaterThan(0))));
    }

    @Test
    void shouldValidateContentLength() throws Exception {
        CreateEntryRequest request = new CreateEntryRequest();
        request.setContent("Short"); // Too short

        mockMvc.perform(post("/api/entries/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllEntriesWithPagination() throws Exception {
        // Create test entries
        for (int i = 0; i < 5; i++) {
            CreateEntryRequest request = new CreateEntryRequest();
            request.setContent("Test entry number " + i + ". This is a longer content to meet validation requirements.");
            
            mockMvc.perform(post("/api/entries/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        mockMvc.perform(get("/api/entries")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(5)));
    }
}
