package com.hackathon.accelerator.activity;

import android.os.Bundle;
import android.util.Log;

import com.hackathon.accelerator.R;
import com.hackathon.accelerator.model.ClassWithScore;
import com.hackathon.accelerator.model.chatbot.Message;
import com.hackathon.accelerator.model.database.Food;
import com.hackathon.accelerator.model.database.Ingredient;
import com.hackathon.accelerator.model.nlp.ContextData;
import com.hackathon.accelerator.service.ChatBot;
import com.hackathon.accelerator.service.CloudDatabase;
import com.hackathon.accelerator.service.NLP;
import com.hackathon.accelerator.utility.SharedPreferencesOperations;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.model.chatbot.ChatBotResult.ALLERGIC;
import static com.hackathon.accelerator.model.chatbot.ChatBotResult.NON_ALLERGIC;
import static com.hackathon.accelerator.model.chatbot.ChatBotResult.NOT_FOUND;
import static com.hackathon.accelerator.utility.Constants.CHAT_BOT_FOOD_INTENT;
import static com.hackathon.accelerator.utility.Constants.CHAT_BOT_GOODBYE_INTENT;
import static com.hackathon.accelerator.utility.Constants.CHAT_BOT_INITIAL_MESSAGE;
import static com.hackathon.accelerator.utility.Constants.NATURAL_LANGUAGE_UNDERSTANDING_THRESHOLD;
import static com.hackathon.accelerator.utility.Constants.NLP_DRINK_CATEGORY;
import static com.hackathon.accelerator.utility.Constants.NLP_FOOD_CATEGORY;
import static com.hackathon.accelerator.utility.Constants.bot;
import static com.hackathon.accelerator.utility.Constants.log;
import static com.hackathon.accelerator.utility.Constants.user;

public class ChatActivity extends BaseActivity {

    // adapter
    private MessagesListAdapter<Message> messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        MessagesList messagesList = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesListAdapter<>(user.getId(), null);
        messagesList.setAdapter(messagesAdapter);
        MessageInput messageInput = findViewById(R.id.inputMessage);
        Log.i(log, "chat activity: user message: " + CHAT_BOT_INITIAL_MESSAGE);
        MessageResponse messageResponse = ChatBot.getInstance().sendMessage(CHAT_BOT_INITIAL_MESSAGE, null);
        for (String responseString : ChatBot.getInstance().getResponses(messageResponse)) {
            messagesAdapter.addToStart(new Message(bot.getId(), bot, responseString), true);
            Log.i(log, "chat activity: bot response: " + responseString);
        }
        messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                String messageInput = input.toString();
                Log.i(log, "chat activity: user message: " + messageInput);
                messagesAdapter.addToStart(new Message(user.getId(), user, messageInput), true);
                MessageResponse messageResponse = ChatBot.getInstance().sendMessage(messageInput, null);
                List<String> responses = ChatBot.getInstance().getResponses(messageResponse);
                for (String responseString : responses) {
                    messagesAdapter.addToStart(new Message(bot.getId(), bot, responseString), true);
                    Log.i(log, "chat activity: bot response: " + responseString);
                }
                if (ChatBot.getInstance().intentFound(messageResponse, CHAT_BOT_GOODBYE_INTENT))
                    finish();
                if (ChatBot.getInstance().intentFound(messageResponse, CHAT_BOT_FOOD_INTENT)) {
                    Log.i(log, "chat activity: nlp text: " + input.toString());
                    AnalysisResults analysisResults = NLP.getInstance().analyze(input.toString());
                    ClassWithScore chosenKeyword = null;
                    Food foundedFood = null;
                    if (NLP.getInstance().categoryFound(analysisResults, NLP_FOOD_CATEGORY) || NLP.getInstance().categoryFound(analysisResults, NLP_DRINK_CATEGORY)) {
                        List<ClassWithScore> filteredResults = new ArrayList<>();
                        for (ClassWithScore classWithScore : NLP.getInstance().getKeywords(analysisResults))
                            if (classWithScore.getScore() >= NATURAL_LANGUAGE_UNDERSTANDING_THRESHOLD)
                                filteredResults.add(classWithScore);
                        Log.i(log, "chat activity: keywords are filtered.");
                        for (ClassWithScore keyword : filteredResults) {
                            Log.i(log, "chat activity: keyword: " + keyword.toString());
                            if (chosenKeyword == null)
                                chosenKeyword = keyword;
                            else if (keyword.getScore() > chosenKeyword.getScore())
                                chosenKeyword = keyword;
                        }
                        if (chosenKeyword != null) {
                            Log.i(log, "chat activity: chosen result: " + chosenKeyword.toString());
                            foundedFood = CloudDatabase.getInstance().getFood(chosenKeyword.getName());
                            if (foundedFood != null) {
                                List<Ingredient> allergenIngredients = new ArrayList<>();
                                List<Ingredient> ingredients = foundedFood.getIngredient();
                                List<String> userAllergens = SharedPreferencesOperations.getUserAllergens(getApplicationContext());
                                List<String> userCrossAllergens = CloudDatabase.getInstance().getCrossAllergensByAllergens(userAllergens);
                                for (Ingredient ingredient : ingredients)
                                    for (String userCrossAllergen : userCrossAllergens)
                                        if (ingredient.getId().equalsIgnoreCase(userCrossAllergen)) {
                                            allergenIngredients.add(ingredient);
                                            Log.i(log, "chat activity: allergen " + userCrossAllergen + " is matched.");
                                        }
                                foundedFood.setDetectedAllergens(allergenIngredients);
                            }
                        }

                        String messageString;
                        if (foundedFood == null)
                            messageString = NOT_FOUND.getMessage();
                        else if (foundedFood.getDetectedAllergens().isEmpty())
                            messageString = NON_ALLERGIC.getMessage();
                        else
                            messageString = ALLERGIC.getMessage();

                        ContextData contextData = null;
                        if (foundedFood != null) {
                            List<String> allIngredients = new ArrayList<>();
                            List<String> allergenIngredients = new ArrayList<>();
                            for (Ingredient ingredient : foundedFood.getIngredient())
                                allIngredients.add(ingredient.getName());
                            if (foundedFood.getDetectedAllergens().size() > 0)
                                for (Ingredient allergenIngredient : foundedFood.getDetectedAllergens())
                                    allergenIngredients.add(allergenIngredient.getName());
                            contextData = new ContextData(foundedFood.getFood_name(), allIngredients, allergenIngredients);
                        }

                        if (contextData != null)
                            Log.i(log, "chat activity: context data: " + contextData.toString());

                        MessageResponse keywordResponses = ChatBot.getInstance().sendMessage(messageString, contextData);
                        for (String responseString : ChatBot.getInstance().getResponses(keywordResponses)) {
                            messagesAdapter.addToStart(new Message(bot.getId(), bot, responseString), true);
                            Log.i(log, "chat activity: bot response: " + responseString);
                        }
                    }
                }
                return true;
            }
        });
    }
}