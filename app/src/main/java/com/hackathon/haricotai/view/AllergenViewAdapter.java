package com.hackathon.haricotai.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.hackathon.haricotai.R;

import java.util.List;

public class AllergenViewAdapter extends ArrayAdapter<AllergenView> {

    private List<AllergenView> allergenViews;
    private Context context;

    public AllergenViewAdapter(Context context, int layout, List<AllergenView> allergenViews) {
        super(context, layout, allergenViews);
        this.context = context;
        this.allergenViews = allergenViews;
    }

    @NonNull
    @Override
    public View getView(int position, View currentView, @NonNull ViewGroup parentView) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.list_allergens, null);
            holder = new ViewHolder();
            holder.allergenName = currentView.findViewById(R.id.text_view_allergen_name);
            holder.allergenCheckBox = currentView.findViewById(R.id.switch_allergen_checkBox);
            currentView.setTag(holder);
            holder.allergenCheckBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Switch allergenSwitch = (Switch) v;
                    AllergenView allergenView = (AllergenView) allergenSwitch.getTag();
                    allergenView.setIsSelected(allergenSwitch.isChecked());
                }
            });
        } else
            holder = (ViewHolder) currentView.getTag();


        AllergenView allergenView = allergenViews.get(position);
        holder.allergenName.setText(allergenView.getText());
        holder.allergenCheckBox.setChecked(allergenView.isSelected());
        holder.allergenCheckBox.setTag(allergenView);
        return currentView;
    }

    private class ViewHolder {
        TextView allergenName;
        Switch allergenCheckBox;
    }

}