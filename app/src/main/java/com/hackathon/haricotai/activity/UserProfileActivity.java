package com.hackathon.haricotai.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.hackathon.haricotai.Constants;
import com.hackathon.haricotai.R;
import com.hackathon.haricotai.Services;
import com.hackathon.haricotai.model.database.Allergen;
import com.hackathon.haricotai.model.database.Food;
import com.hackathon.haricotai.utility.SharedPreferences;
import com.hackathon.haricotai.view.AllergenView;
import com.hackathon.haricotai.view.AllergenViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ArrayList<AllergenView> allergenViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_profile);

        // permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (!checkCameraRequested())
            requestCamera();

        this.allergenViews = new ArrayList<>();
        Button saveButton = (Button) findViewById(R.id.button_save_and_forward);

        this.prepareAllergensList();
        this.fillUserAllergens();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Allergen> selectedAllergens = new ArrayList<>();
                for (AllergenView allergenView : allergenViews)
                    if (allergenView.isSelected())
                        selectedAllergens.add(allergenView.getAllergen());
                List<String> crossAllergenNames = Services.database.findCrossAllergensByAllergens(selectedAllergens);
                SharedPreferences.savePreferences(getApplicationContext(), crossAllergenNames, Constants.SHARED_PREFERENCE_NAME, Constants.SHARED_PREFERENCE_DATA_NAME);
                goToImageCaptureActivity();
            }
        });
    }

    private void prepareAllergensList() {
        List<Allergen> allergens = Services.database.getAllAllergens();
        for (Allergen allergen : allergens) {
            AllergenView allergenView = new AllergenView(allergen);
            this.allergenViews.add(allergenView);
        }
        ArrayAdapter<AllergenView> allergenViewArrayAdapter = new AllergenViewAdapter(this.getApplicationContext(), R.layout.activity_user_profile, allergenViews);
        ListView allergensListView = (ListView) findViewById(R.id.list_view_user_allergens);
        allergensListView.setAdapter(allergenViewArrayAdapter);
    }

    private boolean checkCameraRequested() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCamera() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSIONS_CAMERA);
    }

    private void fillUserAllergens() {
        List<String> userAllergens = SharedPreferences.loadPreferences(this.getApplicationContext(), Constants.SHARED_PREFERENCE_NAME, Constants.SHARED_PREFERENCE_DATA_NAME);
        for (String userAllergen : userAllergens) {
            for (AllergenView allergenView : allergenViews) {
                if (allergenView.getCode().equalsIgnoreCase(userAllergen)) {
                    allergenView.setIsSelected(true);
                }
            }
        }
    }

    private void goToImageCaptureActivity() {
        Intent intent = new Intent(this, ImageCaptureActivity.class);
        startActivityForResult(intent, Constants.REQUEST_ACTIVITY_IMAGE_CAPTURE);
    }

    private void goToChatActivity() {
        Intent chatBotIntent = new Intent(this, ChatActivity.class);
        startActivityForResult(chatBotIntent, Constants.REQUEST_ACTIVITY_CHAT);
    }

    private void goToResultActivity(Food food) {
        Intent resultActivityIntent = new Intent();
        resultActivityIntent.putExtra(Constants.RESULT_TYPE_CAMERA, food);
        this.setResult(Activity.RESULT_OK, resultActivityIntent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ACTIVITY_RESULT: {
                goToImageCaptureActivity();
            }
            case Constants.REQUEST_ACTIVITY_IMAGE_CAPTURE: {
                if (data.getSerializableExtra(Constants.RESULT_TYPE_CAMERA) != null) {
                    Food foundFood = (Food) data.getSerializableExtra(Constants.RESULT_TYPE_CAMERA);
                    goToResultActivity(foundFood);
                } else
                    goToChatActivity();
            }
            case Constants.REQUEST_ACTIVITY_CHAT: {
                // stay at this activity
            }
        }
    }
}
