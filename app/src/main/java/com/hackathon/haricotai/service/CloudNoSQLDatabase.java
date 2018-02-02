package com.hackathon.haricotai.service;

import android.util.Log;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.hackathon.haricotai.Constants;
import com.hackathon.haricotai.Credentials;
import com.hackathon.haricotai.model.database.Allergen;
import com.hackathon.haricotai.model.database.Allergens;
import com.hackathon.haricotai.model.database.CrossAllergen;
import com.hackathon.haricotai.model.database.Food;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CloudNoSQLDatabase {

    private static CloudNoSQLDatabase instance;
    private Database database;

    private CloudNoSQLDatabase() {
        URL connectionURL = generateConnectionURL(Credentials.CLOUNDANT_NOSQL_USERNAME, Credentials.CLOUNDANT_NOSQL_PASSWORD);
        CloudantClient client = ClientBuilder.url(connectionURL).build();
        this.database = client.database(Credentials.CLOUNDANT_NOSQL_DATABASE, true);
    }

    public static CloudNoSQLDatabase getInstance() {
        if (instance == null)
            instance = new CloudNoSQLDatabase();
        return instance;
    }

    private static URL generateConnectionURL(String username, String password) {
        String URLString = "https://" + username + ":" + password + "@" + password + ".cloudant.com";
        URL connectionURL = null;
        try {
            connectionURL = new URL(URLString);
        } catch (MalformedURLException e) {
            Log.i(Constants.LOGGING, "cloud-database: " + e.getMessage());
        }
        return connectionURL;
    }

    public List<Allergen> getAllAllergens() {
        return this.database.find(Allergens.class, "allergens").getAllergens();
    }

    public List<String> findCrossAllergensByAllergens(List<Allergen> allergens) {
        List<String> crossAllergens = new ArrayList<>();
        if (allergens != null) {
            for (Allergen allergen : allergens) {
                CrossAllergen crossAllergen = this.database.find(CrossAllergen.class, allergen.getId());
                if (crossAllergen != null) {
                    crossAllergens.addAll(crossAllergen.getCrossAllergens());
                }
            }
        }
        return crossAllergens;
    }

    public List<Food> getFoodsByNames(List<String> foodNames) {
        List<Food> foods = null;
        if (foodNames != null && !foodNames.isEmpty()) {
            StringBuilder selectorJSON = new StringBuilder("\"selector\": { \"$or\" :[ {\"food_name\": {\"$regex\" : ");
            for (int i = 0; i < foodNames.size(); i++) {
                selectorJSON.append("(?i)");
                selectorJSON.append(foodNames.get(i).replaceAll(" ", "(?i)"));
                selectorJSON.append("}}");
                if (i == foodNames.size() - 1) {
                    selectorJSON.append("]}");
                } else {
                    selectorJSON.append(",{\"food_name\": {\"$regex\" : ");
                }
            }
            foods = this.database.findByIndex(selectorJSON.toString(), Food.class);
        }
        return foods;
    }
}