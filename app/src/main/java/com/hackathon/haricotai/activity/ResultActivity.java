package com.hackathon.haricotai.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.haricotai.Constants;
import com.hackathon.haricotai.R;
import com.hackathon.haricotai.model.Result;
import com.hackathon.haricotai.model.database.Food;
import com.hackathon.haricotai.model.database.Ingredient;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private Result result;
    private Food foundFood;

    private ImageView resultImageView;
    private TextView foundFoodTextView;
    private TextView foundAllergensTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_result);

        this.initialize();
        if (getIntent().getSerializableExtra(Constants.RESULT_TYPE_CHAT) != null)
            this.foundFood = (Food) getIntent().getSerializableExtra(Constants.RESULT_TYPE_CHAT);
        else if (getIntent().getSerializableExtra(Constants.RESULT_TYPE_CAMERA) != null)
            this.foundFood = (Food) getIntent().getSerializableExtra(Constants.RESULT_TYPE_CAMERA);
        if (foundFood != null)
            this.giveResult(foundFood);
        else
            this.finish();
    }

    private void initialize() {
        this.resultImageView = (ImageView) findViewById(R.id.image_view_result);
        this.foundFoodTextView = (TextView) findViewById(R.id.text_view_food);
        this.foundAllergensTextView = (TextView) findViewById(R.id.text_view_allergens);
    }

    private String generateIngredientsCommaFormat(List<Ingredient> ingredients) {
        StringBuilder builder = new StringBuilder("");
        for (Ingredient ingredient : ingredients) {
            if (!builder.toString().equalsIgnoreCase(""))
                builder.append(",");
            builder.append(ingredient.getName());
        }
        return builder.toString();
    }

    private void giveResult(Food foundFood) {
        this.foundFoodTextView.setText(String.format("Food Name\n%s", foundFood.getName()));
        if (foundFood.getDetectedAllergens().size() > 0) {
            this.resultImageView.setImageResource(R.drawable.image_cross);
            this.foundAllergensTextView.setText(String.format("Allergens\n%s", this.generateIngredientsCommaFormat(foundFood.getDetectedAllergens())));
        } else {
            this.resultImageView.setImageResource(R.drawable.image_check);
            this.foundAllergensTextView.setText(Constants.RESULT_OK);
        }
    }
}