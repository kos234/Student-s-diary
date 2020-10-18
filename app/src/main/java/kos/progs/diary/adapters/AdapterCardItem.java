package kos.progs.diary.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import kos.progs.diary.constructors.ConstructorItemCard;
import kos.progs.diary.FixSetTag;
import kos.progs.diary.fragments.FragmentBells;
import kos.progs.diary.fragments.FragmentDnewnik;
import kos.progs.diary.MainActivity;

import kos.progs.diary.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AdapterCardItem extends RecyclerView.Adapter<AdapterCardItem.RecyclerViewHolder> {
    final ArrayList<ConstructorItemCard> constructorItemCards;
    final TextView widthPred;
    final TextView widthDz;
    final TextView widthOcenka;
    final Context context;
    final SharedPreferences settings;
    final SharedPreferences Current_Theme;

    public AdapterCardItem(ArrayList<ConstructorItemCard> constructorItemCards, TextView widthPred, TextView widthDz, TextView widthOcenka, Context context) {
        this.constructorItemCards = constructorItemCards;
        this.widthPred = widthPred;
        this.widthDz = widthDz;
        this.widthOcenka = widthOcenka;
        this.context = context;
        Current_Theme = context.getSharedPreferences("Current_Theme", MODE_PRIVATE);
        settings = context.getSharedPreferences("Settings", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        try {
            final ConstructorItemCard constructorItemCard = constructorItemCards.get(position);
            String[] ids = constructorItemCard.idTextView;
            widthPred.post(() -> {
                try {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthPred.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMarginEnd(MainActivity.dpSize);
                    holder.linear.setLayoutParams(layoutParams);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            if (constructorItemCards.size() - 1 == position)
                holder.tableRow.setPadding(0, 0, 0, 0);

            holder.textViewName.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewName.setTextColor(Current_Theme.getInt("custom_text_dark", ContextCompat.getColor(context, R.color.custom_text_dark)));

            holder.textViewKab.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewKab.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));

            holder.textViewDz.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewDz.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.textViewDz.setTag(new FixSetTag().setNUM_DAY(constructorItemCard.item).setPOSITION_LIST(position));
            widthDz.post(() -> {
                try {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthDz.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.setMarginEnd(MainActivity.dpSize);
                    holder.textViewDz.setLayoutParams(layoutParams);
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            holder.textViewDz.setOnClickListener(v -> {
                try {
                    if (!constructorItemCard.isUmply)
                        ClickSizeDnew(v, constructorItemCard.item);
                    else {
                        FixSetTag fixSetTag = (FixSetTag) v.getTag();
                        createDay((Integer) fixSetTag.getNUM_DAY());
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });


            holder.textViewOcenka.setBackgroundColor(Current_Theme.getInt("custom_card", ContextCompat.getColor(context, R.color.custom_card)));
            holder.textViewOcenka.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            holder.textViewOcenka.setTag(new FixSetTag().setNUM_DAY(constructorItemCard.item).setPOSITION_LIST(position));
            holder.textViewDz.setTag(new FixSetTag().setNUM_DAY(constructorItemCard.item).setPOSITION_LIST(position));
            holder.textViewOcenka.getHeight();
            widthOcenka.post(() -> {
                try {
                    holder.textViewOcenka.setLayoutParams(new LinearLayout.LayoutParams(widthOcenka.getWidth(), LinearLayout.LayoutParams.MATCH_PARENT)); //height is ready
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
            holder.textViewOcenka.setOnClickListener(v -> {
                try {
                    if (!constructorItemCard.isUmply)
                        ClickSizeDnew(v, constructorItemCard.item);
                    else {
                        FixSetTag fixSetTag = (FixSetTag) v.getTag();
                        createDay((Integer) fixSetTag.getNUM_DAY());
                    }
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });

            if (!ids[0].equals("-1")) {
                if (ids[0].equals(String.valueOf(position)) && ids[1].equals(String.valueOf(constructorItemCard.item))) {
                    if (ids[2].equals(String.valueOf(holder.textViewOcenka.getId())))
                        ClickSizeDnew(holder.textViewOcenka, constructorItemCard.item);
                    else if (ids[2].equals(String.valueOf(holder.textViewDz.getId())))
                        ClickSizeDnew(holder.textViewDz, constructorItemCard.item);
                }
            }

            holder.textViewName.setText(constructorItemCard.name);
            holder.textViewKab.setText(constructorItemCard.kab);
            holder.textViewDz.setText(constructorItemCard.dz);
            holder.textViewOcenka.setText(constructorItemCard.ocenka);

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    @Override
    public int getItemCount() {
        try {
            return constructorItemCards.size();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
            return 0;
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewName, textViewKab, textViewDz, textViewOcenka;
        final LinearLayout linear, tableRow;


        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_name_dnev);
            textViewKab = itemView.findViewById(R.id.textView_kab_dnev);
            textViewDz = itemView.findViewById(R.id.textView_dnev_dz);
            textViewOcenka = itemView.findViewById(R.id.textView_dnev_ocenka);
            linear = itemView.findViewById(R.id.linear);
            tableRow = itemView.findViewById(R.id.tableRow);
        }
    }

    public void createDay(int dayIndex) {
        try {
            Fragment fragmentActiv = (Fragment) FragmentBells.class.newInstance();
            Bundle bundle = ((MainActivity) context).getBundles.get(1);
            bundle.putStringArray("action", new String[]{"add", String.valueOf(dayIndex)});
            bundle.putStringArray("currentWindow", new String[]{"create", String.valueOf(dayIndex)});
            fragmentActiv.setArguments(bundle);
            ((MainActivity) context).fragmentManager.beginTransaction().addToBackStack("q").replace(R.id.Smena, fragmentActiv).commit();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Day", context.getResources().getStringArray(R.array.DayTxt)[dayIndex]);
            editor.apply();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void ClickSizeDnew(final View view, final int position) {
        try {
            final FragmentDnewnik fragmentDnewnik = (FragmentDnewnik) ((MainActivity) context).fragmentManager.getFragments().get(0);
            final FixSetTag tags = (FixSetTag) view.getTag();
            fragmentDnewnik.currentWindow = new String[]{"edit", String.valueOf(tags.getPOSITION_LIST()), String.valueOf(tags.getNUM_DAY()), String.valueOf(view.getId())};
            final View promptsView = LayoutInflater.from(context).inflate(R.layout.input_edittext, null);
            final AlertDialog.Builder input = new AlertDialog.Builder(context);
            input.setView(promptsView);
            GradientDrawable alertbackground = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.corners_alert);
            Objects.requireNonNull(alertbackground).setColor(Current_Theme.getInt("custom_background", ContextCompat.getColor(context, R.color.custom_background)));
            if (settings.getBoolean("BorderAlertSettings", false))
                alertbackground.setStroke(settings.getInt("dpBorderSettings", 4) * MainActivity.dpSize, Current_Theme.getInt("custom_color_block_choose_border", ContextCompat.getColor(context, R.color.custom_color_block_choose_border)));
            promptsView.findViewById(R.id.input_edittext_linear).setBackground(alertbackground);

            fragmentDnewnik.alertDialog = input.create();
            final TextView textView = (TextView) view;

            final EditText editText = promptsView.findViewById(R.id.input_edittext);
            editText.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
            editText.setText(textView.getText());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MainActivity.setCursorColorsQ(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            } else {
                MainActivity.setCursorColor(editText, Current_Theme.getInt("custom_cursor", ContextCompat.getColor(context, R.color.custom_cursor)));
            }
            final boolean isDz;
            if (view.getId() == R.id.textView_dnev_ocenka) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                isDz = false;
            } else isDz = true;
            TextView button = promptsView.findViewById(R.id.input_edittext_button);
            button.setTextColor(Current_Theme.getInt("custom_button_act", ContextCompat.getColor(context, R.color.custom_button_act)));
            button.setOnClickListener(v -> {
                try {
                    textView.setText(editText.getText());
                    fragmentDnewnik.alertDialog.hide();
                    fragmentDnewnik.alertDialog = null;
                    final String textWrite;
                    String[] temp4 = editText.getText().toString().split("\n");
                    if (temp4.length == 1)
                        textWrite = temp4[0];
                    else {
                        StringBuilder temp = new StringBuilder();
                        for (int n = 0; n < temp4.length; n++) {
                            if (n + 1 == temp4.length)
                                temp.append(temp4[n]);
                            else
                                temp.append(temp4[n]).append("/*/");
                        }
                        textWrite = temp.toString();
                    }
                    try {
                        File mFolder = new File(context.getFilesDir() + "/dnewnik");
                        if (!mFolder.exists()) {
                            mFolder.mkdir();
                        }
                        File FileTxt = new File(mFolder.getAbsolutePath() + "/" + (fragmentDnewnik.startNedeli + (int) tags.getNUM_DAY()) + "." + fragmentDnewnik.startMes + "." + fragmentDnewnik.year + ".txt");
                        if (!FileTxt.exists()) {
                            FileTxt.createNewFile();
                        }
                        FileInputStream read = new FileInputStream(FileTxt);
                        InputStreamReader reader = new InputStreamReader(read);
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        String temp_read;
                        StringBuilder stringBuilder = new StringBuilder();
                        String[] help, temp;
                        List<String> stringList;

                        boolean isFileNotFound = true;
                        for (int i = 0; (temp_read = bufferedReader.readLine()) != null; i++) {
                            if (i == (int) tags.getPOSITION_LIST()) {
                                help = temp_read.split("=");
                                stringList = fragmentDnewnik.adapterPagerInCard.ConstructorDnewniks.get(position).strings;
                                temp = stringList.get(i).split("=");
                                if (isDz) {
                                    stringBuilder.append(help[0]).append("=").append(help[1]).append("=").append(textWrite).append("=").append(help[3]);
                                    stringList.set(i, temp[0] + "=" + temp[1] + "=" + textWrite + "=" + temp[3]);
                                } else {
                                    stringBuilder.append(help[0]).append("=").append(help[1]).append("=").append(help[2]).append("=").append(textWrite);
                                    stringList.set(i, temp[0] + "=" + temp[1] + "=" + temp[2] + "=" + textWrite);
                                }
                                fragmentDnewnik.adapterPagerInCard.ConstructorDnewniks.get(position).strings = stringList;

                                stringBuilder.append("\n");
                            } else stringBuilder.append(temp_read).append("\n");

                            isFileNotFound = false;
                        }
                        if (isFileNotFound)
                            throw new FileNotFoundException();

                        try {
                            FileOutputStream write = new FileOutputStream(FileTxt);
                            String temp_write = stringBuilder.toString();

                            write.write(temp_write.getBytes());
                            write.close();
                        } catch (IOException p) {
                            p.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fragmentDnewnik.currentWindow = new String[]{"null"};
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });


            Objects.requireNonNull(fragmentDnewnik.alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            fragmentDnewnik.alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = fragmentDnewnik.alertDialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
            wmlp.y = 15 * MainActivity.dpSize;

            fragmentDnewnik.alertDialog.show();
            fragmentDnewnik.alertDialog.setOnCancelListener(dialog -> {
                try {
                    fragmentDnewnik.currentWindow = new String[]{"null"};
                    fragmentDnewnik.alertDialog = null;
                } catch (Exception error) {
                    ((MainActivity) context).errorStack(error);
                }
            });
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }
}
