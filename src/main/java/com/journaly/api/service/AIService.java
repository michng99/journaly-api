package com.journaly.api.service;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final TextAnalyticsClient client;

    public AIService(@Value("${ai.service.key}") String apiKey,
                     @Value("${ai.service.endpoint}") String endpoint) {
        this.client = new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();
    }

    public DocumentSentiment analyzeSentiment(String text) {
        return client.analyzeSentiment(text);
    }
}