package com.hackathon.accelerator.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.hackathon.accelerator.R;
import com.hackathon.accelerator.model.database.Allergen;
import com.hackathon.accelerator.service.CloudDatabase;
import com.hackathon.accelerator.utility.SharedPreferencesOperations;
import com.hackathon.accelerator.view.AllergenView;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.accelerator.utility.Constants.REQUEST_READ_CONTACTS;
import static com.hackathon.accelerator.utility.Constants.log;

public class ProfileActivity extends BaseActivity {

    private ArrayList<AllergenView> allergenViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allergenViews = new ArrayList<>();
        setContentView(R.layout.activity_profile);
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_CONTACTS);
        Button buttonSave = findViewById(R.id.button_save_and_continue);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Allergen> selectedAllergenList = new ArrayList<>();
                for (AllergenView allergenView : allergenViews)
                    if (allergenView.isSelected())
                        selectedAllergenList.add(allergenView.getAllergen());
                SharedPreferencesOperations.saveUserAllergens(getApplicationContext(), selectedAllergenList);
                Log.i(log, "profile activity: selected allergens: " + selectedAllergenList.toString());
                Intent imageCaptureActivity = new Intent(getApplicationContext(), ImageCaptureActivity.class);
                startActivity(imageCaptureActivity);
                finish();
            }
        });
        List<Allergen> allergens = null;
        try {
            allergens = CloudDatabase.getInstance().getAllergens();
            Log.i(log, "profile activity: all allergens: " + allergens.toString());
        } catch (Exception ignored) {
        }
        if (allergens != null)
            for (Allergen allergen : allergens)
                allergenViews.add(new AllergenView(allergen));
        ArrayAdapter<AllergenView> allergenViewAdapter = new AllergenViewAdapter(this, allergenViews);
        ListView allergenViewListView = findViewById(R.id.list_view_allergens);
        allergenViewListView.setAdapter(allergenViewAdapter);
        for (String allergen : SharedPreferencesOperations.getUserAllergens(this))
            for (AllergenView allergenView : allergenViews)
                if (allergenView.getCode().equalsIgnoreCase(allergen))
                    allergenView.setIsSelected(true);
    }

    private class AllergenViewAdapter extends ArrayAdapter<AllergenView> {
        private ArrayList<AllergenView> allergenViewArrayList;

        AllergenViewAdapter(Context context, ArrayList<AllergenView> allergenViewList) {
            super(context, R.layout.activity_profile, allergenViewList);
            allergenViewArrayList = allergenViewList;
        }

        @SuppressWarnings("NullableProblems")
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View list_item, @SuppressWarnings("NullableProblems") ViewGroup list) {
            ViewHolder holder;

            if (list_item == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //noinspection ConstantConditions
                list_item = vi.inflate(R.layout.list_item_allergen, null);
                holder = new ViewHolder();
                holder.allergenTextView = list_item.findViewById(R.id.text_view_allergen_name);
                holder.selectSwitch = list_item.findViewById(R.id.switch_allergen_check_box);
                list_item.setTag(holder);
            } else
                holder = (ViewHolder) list_item.getTag();

            final AllergenView allergenView = allergenViewArrayList.get(position);
            holder.allergenTextView.setText(allergenView.getText());
            holder.selectSwitch.setText("");
            holder.selectSwitch.setChecked(allergenView.isSelected());
            holder.selectSwitch.setTag(allergenView);
            holder.selectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    allergenView.setIsSelected(isChecked);
                    Log.i(log, "profile activity: allergen view: " + allergenView.getText() + " checked: " + isChecked);
                }
            });

            return list_item;
        }

        private class ViewHolder {
            TextView allergenTextView;
            Switch selectSwitch;
        }
    }
}
