package com.journaly.api.service;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.ai.textanalytics.models.TextSentiment;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.IterableStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AIService {

    private final TextAnalyticsClient client;
    private final boolean isConfigured;

    public AIService(@Value("${ai.service.key:}") String apiKey,
                     @Value("${ai.service.endpoint:}") String endpoint) {
        
        // Check if Azure AI service is properly configured
        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-azure-key-here") &&
            endpoint != null && !endpoint.isEmpty() && !endpoint.equals("https://your-endpoint.cognitiveservices.azure.com/")) {
            
            this.client = new TextAnalyticsClientBuilder()
                    .credential(new AzureKeyCredential(apiKey))
                    .endpoint(endpoint)
                    .buildClient();
            this.isConfigured = true;
            log.info("Azure AI Text Analytics client configured successfully");
        } else {
            this.client = null;
            this.isConfigured = false;
            log.warn("Azure AI Text Analytics not configured. Using fallback sentiment analysis.");
        }
    }

    @Cacheable(value = "sentiments", key = "#text.hashCode()")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public DocumentSentiment analyzeSentiment(String text) {
        if (isConfigured && client != null) {
            try {
                log.debug("Calling Azure AI service for sentiment analysis");
                return client.analyzeSentiment(text);
            } catch (Exception e) {
                log.error("Error calling Azure AI service: {}", e.getMessage());
                return createFallbackSentiment();
            }
        } else {
            log.info("Using fallback sentiment analysis");
            return createFallbackSentiment();
        }
    }

    @Async
    @Cacheable(value = "sentiments", key = "#text.hashCode()")
    public CompletableFuture<DocumentSentiment> analyzeSentimentAsync(String text) {
        if (isConfigured && client != null) {
            try {
                log.debug("Calling Azure AI service asynchronously for sentiment analysis");
                DocumentSentiment result = client.analyzeSentiment(text);
                return CompletableFuture.completedFuture(result);
            } catch (Exception e) {
                log.error("Error calling Azure AI service async: {}", e.getMessage());
                return CompletableFuture.completedFuture(createFallbackSentiment());
            }
        } else {
            log.info("Using fallback sentiment analysis async");
            return CompletableFuture.completedFuture(createFallbackSentiment());
        }
    }

    private DocumentSentiment createFallbackSentiment() {
        // Create a neutral sentiment as fallback
        SentimentConfidenceScores scores = new SentimentConfidenceScores(0.33, 0.33, 0.34);
        return new DocumentSentiment(
            TextSentiment.NEUTRAL, 
            scores, 
            new IterableStream<>(java.util.Collections.emptyList()), 
            new IterableStream<>(java.util.Collections.emptyList())
        );
    }
}