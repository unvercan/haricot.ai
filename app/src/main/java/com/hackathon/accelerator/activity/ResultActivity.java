package com.hackathon.accelerator.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackathon.accelerator.R;
import com.hackathon.accelerator.model.database.Food;

import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_ALLERGIC_RESULT_DETAIL_LABEL;
import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_FOOD_FOUND_RESULT_LABEL;
import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_FOOD_NOT_FOUND_RESULT_LABEL;
import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_NON_ALLERGIC_RESULT_DETAIL_LABEL;
import static com.hackathon.accelerator.utility.Constants.IMAGE_CLASSIFIER_RESULT_DATA;

public class ResultActivity extends BaseActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView resultImageView = findViewById(R.id.image_view_result_image);
        TextView resultLabelTextView = findViewById(R.id.text_view_result_label);
        TextView resultTextView = findViewById(R.id.text_view_result);
        TextView resultDetailLabelTextView = findViewById(R.id.text_view_result_detail_label);
        ListView resultDetailListView = findViewById(R.id.list_view_result_detail);
        if (getIntent().getSerializableExtra(IMAGE_CLASSIFIER_RESULT_DATA) != null) {
            Food foundedFood = (Food) getIntent().getSerializableExtra(IMAGE_CLASSIFIER_RESULT_DATA);
            if (foundedFood.getDetectedAllergens().size() > 0) {
                resultTextView.setText(foundedFood.getFood_name());
                resultLabelTextView.setText(IMAGE_CLASSIFIER_FOOD_FOUND_RESULT_LABEL);
                resultImageView.setImageResource(R.drawable.cross_image);
                resultDetailLabelTextView.setText(IMAGE_CLASSIFIER_ALLERGIC_RESULT_DETAIL_LABEL);
                String[] allergens = new String[foundedFood.getDetectedAllergens().size()];
                for (int i = 0; i < foundedFood.getDetectedAllergens().size(); i++)
                    allergens[i] = foundedFood.getDetectedAllergens().get(i).getName();
                ArrayAdapter<String> allergensAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, allergens);
                resultDetailListView.setAdapter(allergensAdapter);
            } else {
                resultTextView.setText(foundedFood.getFood_name());
                resultLabelTextView.setText(IMAGE_CLASSIFIER_FOOD_FOUND_RESULT_LABEL);
                resultDetailLabelTextView.setText(IMAGE_CLASSIFIER_NON_ALLERGIC_RESULT_DETAIL_LABEL);
                resultImageView.setImageResource(R.drawable.check_image);
                resultDetailListView.setVisibility(View.INVISIBLE);
            }
        } else {
            resultLabelTextView.setText(IMAGE_CLASSIFIER_FOOD_NOT_FOUND_RESULT_LABEL);
            resultImageView.setImageResource(R.drawable.undefined_image);
            resultTextView.setVisibility(View.INVISIBLE);
            resultDetailLabelTextView.setVisibility(View.INVISIBLE);
            resultDetailListView.setVisibility(View.INVISIBLE);
        }
    }
}