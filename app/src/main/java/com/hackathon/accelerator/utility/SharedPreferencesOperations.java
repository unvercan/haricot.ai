package com.hackathon.accelerator.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hackathon.accelerator.model.database.Allergen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hackathon.accelerator.utility.Constants.PREFERENCES_DATA;
import static com.hackathon.accelerator.utility.Constants.PREFERENCES_NAME;
import static com.hackathon.accelerator.utility.Constants.log;

public class SharedPreferencesOperations {

    public static List<String> getUserAllergens(Context context) {
        List<String> userAllergens = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        Set<String> allergensSet = preferences.getStringSet(PREFERENCES_DATA, null);
        if (allergensSet != null)
            userAllergens.addAll(allergensSet);
        if (userAllergens.isEmpty())
            Log.i(log, "shared preferences operations: there is no selected allergens.");
        else
            Log.i(log, "shared preferences operations: user allergens: " + userAllergens.toString());
        return userAllergens;
    }

    public static void saveUserAllergens(Context context, List<Allergen> selectedAllergens) {
        Log.i(log, "shared preferences operations: user allergens: " + selectedAllergens.toString());
        Set<String> allergensSet = new HashSet<>();
        for (Allergen selectedAllergen : selectedAllergens)
            allergensSet.add(selectedAllergen.getName());
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(PREFERENCES_DATA, allergensSet);
        editor.apply();
        Log.i(log, "shared preferences operations: user allergens are saved.");
    }
}
