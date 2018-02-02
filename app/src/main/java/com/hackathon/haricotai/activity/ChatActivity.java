package com.hackathon.haricotai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.hackathon.haricotai.Commons;
import com.hackathon.haricotai.Constants;
import com.hackathon.haricotai.R;
import com.hackathon.haricotai.Services;
import com.hackathon.haricotai.model.Result;
import com.hackathon.haricotai.model.conversion.Message;
import com.hackathon.haricotai.model.database.Food;
import com.hackathon.haricotai.model.database.Ingredient;
import com.hackathon.haricotai.utility.SharedPreferences;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private MessagesListAdapter<Message> messagesAdapter;
    private MessageInput messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chat);

        // permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.initialize();

        MessageResponse botResponse = Services.conversion.sendMessage("hi", null);
        List<String> responses = Services.conversion.receiveResponses(botResponse);
        for (String response : responses)
            messagesAdapter.addToStart(new Message(Constants.BOT_USER_ID, Constants.BOT_USER, response), true);

        this.messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence userMessageInput) {
                messagesAdapter.addToStart(new Message(Constants.APP_USER_ID, Constants.APP_USER, userMessageInput.toString()), true);
                MessageResponse botResponse = Services.conversion.sendMessage(userMessageInput.toString(), null);
                Commons.CONVERSATION_INTENT_FOOD = Services.conversion.checkIntentExist(botResponse, "food");
                Commons.CONVERSATION_INTENT_GOODBYE = Services.conversion.checkIntentExist(botResponse, "goodbye");
                if (!Commons.CONVERSATION_INTENT_FOOD) {
                    List<String> responses = Services.conversion.receiveResponses(botResponse);
                    for (String response : responses)
                        messagesAdapter.addToStart(new Message(Constants.BOT_USER_ID, Constants.BOT_USER, response), true);
                }
                if (Commons.CONVERSATION_INTENT_GOODBYE)
                    finish();
                Commons.CONVERSATION_INTENT_GOODBYE = false;
                if (Commons.CONVERSATION_INTENT_FOOD) {
                    AnalysisResults analysisResults = Services.nlp.analyzeText(userMessageInput.toString());
                    for (String nlpCategory : Constants.NLP_DEMANDED_CATEGORIES)
                        Commons.NLP_DEMANDED_CATEGORIES_FOUND = Commons.NLP_DEMANDED_CATEGORIES_FOUND || Services.nlp.checkCategoryExists(analysisResults, nlpCategory);
                    if (Commons.NLP_DEMANDED_CATEGORIES_FOUND) {
                        List<Result> results = convertKeywordWithRelevancesToResults(Services.nlp.getKeywordWithRelevances(analysisResults));
                        List<Food> foods = filterFoodsByUserAllergens(filterNLPResults(results));
                        List<String> responses;
                        if (foods.size() > 0) {
                            MessageResponse response = Services.conversion.sendMessage("allergic", generateNLPResultForChatBot(foods, true));
                            Commons.CONVERSATION_INTENT_ALLERGIC = Services.conversion.checkIntentExist(response, "allergic");
                            responses = Services.conversion.receiveResponses(response);
                        } else {
                            MessageResponse messageResponse = Services.conversion.sendMessage("nonallergic", generateNLPResultForChatBot(foods, false));
                            Commons.CONVERSATION_INTENT_NONALLERGIC = Services.conversion.checkIntentExist(messageResponse, "nonallergic");
                            responses = Services.conversion.receiveResponses(messageResponse);
                        }
                        for (String response : responses)
                            messagesAdapter.addToStart(new Message(Constants.BOT_USER_ID, Constants.BOT_USER, response), true);

                        goToResultActivity(foods);
                    }
                }
                Commons.CONVERSATION_INTENT_FOOD = false;
                Commons.NLP_DEMANDED_CATEGORIES_FOUND = false;
                return true;
            }
        });
    }

    private void initialize() {
        MessagesList messagesList = (MessagesList) findViewById(R.id.message_list);
        this.messagesAdapter = new MessagesListAdapter<>(Constants.APP_USER_ID, null);
        messagesList.setAdapter(this.messagesAdapter);
        this.messageInput = (MessageInput) findViewById(R.id.message_input);
    }

    private void goToResultActivity(List<Food> resultFoods) {
        Intent resultActivityIntent = new Intent(this, Result.class);
        Food resultFood = null;
        if (resultFoods.size() > 0)
            resultFood = resultFoods.get(0);
        resultActivityIntent.putExtra(Constants.RESULT_TYPE_CHAT, resultFood);
        this.setResult(Activity.RESULT_OK, resultActivityIntent);
        this.startActivity(resultActivityIntent);
    }

    private List<Map.Entry<String, String[]>> generateNLPResultForChatBot(List<Food> foods, boolean allergensFound) {
        List<Map.Entry<String, String[]>> NLPResultMetaData = new ArrayList<>();
        NLPResultMetaData.add(new AbstractMap.SimpleEntry<>("food", this.getFoodNames(foods)));
        NLPResultMetaData.add(new AbstractMap.SimpleEntry<>("ingredients", this.getIngredients(foods)));
        if (allergensFound)
            NLPResultMetaData.add(new AbstractMap.SimpleEntry<>("allergens", this.getMatchedIngredients(foods)));
        return NLPResultMetaData;
    }

    private String[] getMatchedIngredients(List<Food> foods) {
        String[] matchedIngredients = new String[this.numberOfMatchedIngredients(foods)];
        int index = 0;
        for (Food food : foods) {
            for (Ingredient matchedIngredient : food.getDetectedAllergens()) {
                matchedIngredients[index] = matchedIngredient.getName();
                index++;
            }
        }
        return matchedIngredients;
    }

    private String[] getFoodNames(List<Food> foods) {
        String[] foodNames = new String[foods.size()];
        int index = 0;
        for (Food food : foods) {
            foodNames[index] = food.getName();
            index++;
        }
        return foodNames;
    }

    private String[] getIngredients(List<Food> foods) {
        String[] ingredients = new String[this.numberOfIngredients(foods)];
        int index = 0;
        for (Food food : foods) {
            for (Ingredient ingredient : food.getIngredients()) {
                ingredients[index] = ingredient.getName();
                index++;
            }
        }
        return ingredients;
    }

    private int numberOfMatchedIngredients(List<Food> foods) {
        int numberOfMatchedIngredients = 0;
        for (Food food : foods)
            numberOfMatchedIngredients = numberOfMatchedIngredients + food.getDetectedAllergens().size();
        return numberOfMatchedIngredients;
    }

    private int numberOfIngredients(List<Food> foods) {
        int numberOfIngredients = 0;
        for (Food food : foods)
            numberOfIngredients = numberOfIngredients + food.getIngredients().size();
        return numberOfIngredients;
    }

    private List<Result> convertKeywordWithRelevancesToResults(List<Map.Entry<String, Float>> keywordWithRelevances) {
        List<Result> results = new ArrayList<>();
        for (Map.Entry<String, Float> keywordWithRelevance : keywordWithRelevances)
            results.add(new Result(keywordWithRelevance.getKey(), keywordWithRelevance.getValue()));
        return results;
    }

    private List<String> filterNLPResults(List<Result> results) {
        List<String> filteredResults = new ArrayList<>();
        for (Result result : results)
            if (result.getScore() >= Constants.NATURAL_LANGUAGE_UNDERSTANDING_THRESHOLD)
                filteredResults.add(result.getName());
        return filteredResults;
    }

    private List<Food> filterFoodsByUserAllergens(List<String> foodNames) {
        List<Food> foods = Services.database.getFoodsByNames(foodNames);
        List<String> userAllergens = SharedPreferences.loadPreferences(this.getApplicationContext(), Constants.SHARED_PREFERENCE_NAME, Constants.SHARED_PREFERENCE_DATA_NAME);
        List<Food> detectedAllergenFoods = new ArrayList<>();
        if (foods != null) {
            for (Food food : foods) {
                for (Ingredient allergen : food.getIngredients()) {
                    if (userAllergens.contains(allergen.getId())) {
                        food.getDetectedAllergens().add(allergen);
                        if (!detectedAllergenFoods.contains(food)) {
                            detectedAllergenFoods.add(food);
                        }
                    }
                }
            }
        }
        return detectedAllergenFoods;
    }
}
