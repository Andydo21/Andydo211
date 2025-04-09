package com.andd.DoDangAn.DoDangAn.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ImdbService {
    private final RestTemplate restTemplate;
    private final String omdbApiKey = "your_api_key";
    private final String omdbUrl = "http://www.omdbapi.com/?i={imdbId}&apikey={apiKey}";

    public ImdbService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Double getImdbRating(String imdbId) {
        try {
            String url = omdbUrl.replace("{imdbId}", imdbId).replace("{apiKey}", omdbApiKey);
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            return response.path("imdbRating").asDouble();
        } catch (Exception e) {
            return null;
        }
    }
}
