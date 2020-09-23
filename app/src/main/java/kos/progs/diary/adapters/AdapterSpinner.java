package kos.progs.diary.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import kos.progs.diary.MainActivity;
import kos.progs.diary.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdapterSpinner extends ArrayAdapter<String> {
    private final SharedPreferences Current_Theme;
    private final boolean TypeColor;

    public AdapterSpinner(@NonNull Context context, List<String> value, boolean TypeColor) {
        super(context, R.layout.spinner_list, value);
        Current_Theme = getContext().getSharedPreferences("Current_Theme", MODE_PRIVATE);
        this.TypeColor = TypeColor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return viewGenerate(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return viewGenerate(position, convertView, parent);
    }

    private View viewGenerate(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_list, parent, false);
            }
            if(position == 13) {
                String s = "s";
                System.out.println(s);
            }
            String string = getItem(position);

            if (string != null) {
                TextView Text = convertView.findViewById(R.id.text_spinner);
                Text.setText(string);
                Text.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(getContext(), R.color.custom_text_light)));
                if (TypeColor)
                    Text.setBackgroundColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(getContext(), R.color.custom_background)));
                else
                    Text.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(getContext(), R.color.custom_card)));
            }
        } catch (Exception error) {
            ((MainActivity) getContext()).errorStack(error);
        }

        return convertView;
    }
}