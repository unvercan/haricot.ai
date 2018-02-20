package com.hackathon.accelerator.service;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.hackathon.accelerator.model.database.Allergen;
import com.hackathon.accelerator.model.database.Allergens;
import com.hackathon.accelerator.model.database.CrossAllergen;
import com.hackathon.accelerator.model.database.Food;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Credentials.CLOUD_DATABASE_CONNECTION;
import static com.hackathon.accelerator.utility.Credentials.CLOUD_DATABASE_NAME;

public class CloudDatabase {

    private static CloudDatabase instance;
    private Database service;

    private CloudDatabase() {
        URL connectionUrl = null;
        CloudantClient client = null;
        try {
            connectionUrl = new URL(CLOUD_DATABASE_CONNECTION);
        } catch (Exception ignored) {
        }
        if (connectionUrl != null)
            client = ClientBuilder.url(connectionUrl).build();
        if (client != null)
            this.service = client.database(CLOUD_DATABASE_NAME, true);
    }

    public static CloudDatabase getInstance() {
        if (instance == null)
            instance = new CloudDatabase();
        return instance;
    }

    public Food getFood(String name) {
        Food food = null;
        try {
            food = service.find(Food.class, name);
        } catch (Exception ignored) {
        }
        return food;
    }

    public List<Allergen> getAllergens() {
        List<Allergen> allergens = null;
        try {
            allergens = service.find(Allergens.class, "allergens").getAllergen_list();
        } catch (Exception ignored) {
        }
        return allergens;
    }

    public List<String> getCrossAllergensByAllergens(List<String> allergens) {
        List<Allergen> allAllergens = getAllergens();
        List<Allergen> demandedAllergens = new ArrayList<>();
        for (Allergen allergen : allAllergens)
            for (String allergenString : allergens)
                if (allergen.getName().equalsIgnoreCase(allergenString))
                    demandedAllergens.add(allergen);
        List<String> crossAllergenList = new ArrayList<>();
        for (Allergen allergen : demandedAllergens) {
            CrossAllergen crossAllergens = null;
            try {
                crossAllergens = service.find(CrossAllergen.class, allergen.getId());
            } catch (Exception ignored) {
            }
            if (crossAllergens != null)
                crossAllergenList.addAll(crossAllergens.getCrossAllergen());
        }
        if (crossAllergenList.isEmpty())
            crossAllergenList.addAll(allergens);
        return crossAllergenList;
    }
}