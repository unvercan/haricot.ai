package com.hackathon.haricotai.service;

import com.hackathon.haricotai.Credentials;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NaturalLanguageProcessor {

    private static NaturalLanguageProcessor instance;
    private NaturalLanguageUnderstanding service;

    private NaturalLanguageProcessor() {
        this.service = new NaturalLanguageUnderstanding(
                Credentials.NATURAL_LANGUAGE_UNDERSTANDING_API_VERSION,
                Credentials.NATURAL_LANGUAGE_UNDERSTANDING_API_USERNAME,
                Credentials.NATURAL_LANGUAGE_UNDERSTANDING_API_PASSWORD
        );
    }

    public static NaturalLanguageProcessor getInstance() {
        if (instance == null)
            instance = new NaturalLanguageProcessor();
        return instance;
    }

    public boolean checkCategoryExists(AnalysisResults analysisResults, String demandedCategory) {
        List<CategoriesResult> categories = analysisResults.getCategories();
        for (CategoriesResult category : categories) {
            String categoryName = category.getLabel().replace("/", " ");
            if (demandedCategory.equalsIgnoreCase(categoryName))
                return true;
        }
        return false;
    }

    public AnalysisResults analyzeText(String text) {
        CategoriesOptions categoriesOptions = new CategoriesOptions();
        KeywordsOptions keywordsOptions = new KeywordsOptions.Builder().sentiment(true).build();
        Features features = new Features.Builder().keywords(keywordsOptions).categories(categoriesOptions).build();
        AnalyzeOptions analyzeOptions = new AnalyzeOptions.Builder().text(text).features(features).build();
        return service.analyze(analyzeOptions).execute();
    }

    public List<Map.Entry<String, Float>> getKeywordWithRelevances(AnalysisResults analysisResults) {
        List<Map.Entry<String, Float>> keywordWithRelevances = new ArrayList<>();
        List<KeywordsResult> keywordsResults = analysisResults.getKeywords();
        for (KeywordsResult keywordsResult : keywordsResults) {
            String keyword = keywordsResult.getText().replace("/", " ");
            float relevance = keywordsResult.getRelevance().floatValue();
            keywordWithRelevances.add(new AbstractMap.SimpleEntry<>(keyword, relevance));
        }
        return keywordWithRelevances;
    }
}