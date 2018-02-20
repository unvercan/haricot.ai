package com.hackathon.accelerator.service;

import android.util.Log;

import com.hackathon.accelerator.model.ClassWithScore;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Constants.log;
import static com.hackathon.accelerator.utility.Credentials.NLP_PASSWORD;
import static com.hackathon.accelerator.utility.Credentials.NLP_USERNAME;
import static com.hackathon.accelerator.utility.Credentials.NLP_VERSION;

public class NLP {

    private static NLP instance;
    private NaturalLanguageUnderstanding service;

    private NLP() {
        this.service = new NaturalLanguageUnderstanding(NLP_VERSION, NLP_USERNAME, NLP_PASSWORD);
    }

    public static NLP getInstance() {
        if (instance == null)
            instance = new NLP();
        return instance;
    }

    public AnalysisResults analyze(String text) {
        Features features = new Features.Builder()
                .keywords(new KeywordsOptions.Builder()
                        .sentiment(true)
                        .build())
                .categories(new CategoriesOptions())
                .build();
        AnalysisResults analysisResults = service.analyze(
                new AnalyzeOptions.Builder()
                        .text(text)
                        .features(features)
                        .build())
                .execute();
        Log.i(log, "nlp: text is analyzed.");
        return analysisResults;
    }

    private List<String> getCategories(AnalysisResults analysisResults) {
        List<String> categories = new ArrayList<>();
        for (CategoriesResult category : analysisResults.getCategories()) {
            categories.add(stringFix(category.getLabel()));
            Log.i(log, "nlp: category: " + stringFix(category.getLabel()));
        }

        return categories;
    }

    public List<ClassWithScore> getKeywords(AnalysisResults analysisResults) {
        List<ClassWithScore> keywords = new ArrayList<>();
        for (KeywordsResult keyword : analysisResults.getKeywords()) {
            ClassWithScore classWithScore = new ClassWithScore(stringFix(keyword.getText()), keyword.getRelevance().floatValue());
            keywords.add(classWithScore);
            Log.i(log, "nlp: keyword: " + classWithScore.toString());
        }
        return keywords;
    }

    public boolean categoryFound(AnalysisResults analysisResults, String demandedCategory) {
        for (String category : getCategories(analysisResults))
            if (category.contains(demandedCategory)) {
                Log.i(log, "nlp: demanded category " + demandedCategory + " is found.");
                return true;
            }
        return false;
    }

    private String stringFix(String keyword) {
        return keyword.replace("/", " ");
    }
}