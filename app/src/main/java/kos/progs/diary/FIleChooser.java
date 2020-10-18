package kos.progs.diary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kos.progs.diary.constructors.ConstructorFile;
import kos.progs.diary.fragments.FragmentSettings;
import kos.progs.diary.adapters.AdapterFile;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class FIleChooser extends Constant {

    private LinearLayout rootView;
    private Context context;
    private SharedPreferences Current_Theme;
    private File currentPath;
    private LinearLayout linearFolders;
    private final ArrayList<ConstructorFile> constructorFiles = new ArrayList<>();
    public final ArrayList<File> history = new ArrayList<>();

    public FIleChooser(Context context) {
        try {
            this.context = context;
            rootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.content_file_chooser, null);
            Current_Theme = Objects.requireNonNull(context).getSharedPreferences("Current_Theme", MODE_PRIVATE);
            currentPath = new File(Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/backups");

            FragmentSettings fragmentSettings = (FragmentSettings) ((MainActivity) context).fragmentManager.getFragments().get(0);
            fragmentSettings.currentWindow = new String[]{"fileChoose", String.valueOf(0), String.valueOf(BottomSheetBehavior.STATE_EXPANDED)};
            linearFolders = rootView.findViewById(R.id.patchs);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public LinearLayout getRootView() {
        onStart();
        return rootView;
    }

    private void onStart() {
        try {
            history.add(currentPath);
            String[] folders = currentPath.getAbsolutePath().split("/");
            for (int i = 0; i < folders.length; i++) {
                TextView textViewFolder = new TextView(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewFolder.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
                }
                textViewFolder.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                textViewFolder.setPadding(MainActivity.dpSize, 0, MainActivity.dpSize, 0);
                textViewFolder.setText(folders[i]);
                textViewFolder.setOnClickListener(view -> onClickTitleFolder(textViewFolder));
                textViewFolder.setTextSize(15);
                linearFolders.addView(textViewFolder);
                if (i + 1 != folders.length) {
                    TextView textViewSplit = new TextView(context);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewSplit.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL);
                    }
                    textViewSplit.setText("/");
                    textViewSplit.setTextColor(Current_Theme.getInt("custom_text_light", ContextCompat.getColor(context, R.color.custom_text_light)));
                    textViewSplit.setPadding(MainActivity.dpSize, 0, MainActivity.dpSize, 0);
                    textViewSplit.setTextSize(15);
                    linearFolders.addView(textViewSplit);
                }
            }
            File[] files = currentPath.listFiles();
            if (files != null) {
                String type;
                String[] format;

                for (File file : files) {
                    format = file.getName().split("\\.");

                    if (file.isDirectory()) type = "folder";
                    else if (format.length != 1) {
                        if (Arrays.asList(videoFormats).contains(format[1])) type = "video";
                        else if (Arrays.asList(photoFormat).contains(format[1])) type = "photo";
                        else if (Arrays.asList(audioFormat).contains(format[1])) type = "audio";
                        else if (Arrays.asList(archiveFormat).contains(format[1])) type = "archive";
                        else type = "file";
                    } else type = "file";

                    constructorFiles.add(new ConstructorFile(type, file.getName()));
                }
            }
            final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerFiles);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            AdapterFile adapterFile = new AdapterFile(constructorFiles, context);
            adapterFile.setOnItemClickListener(new AdapterFile.OnItemClickListener() {
                @Override
                public void onItemClick(int position, TextView view) {
                    switch (view.getTag().toString()) {
                        case "folder":
                            onClickFolder(view);
                            break;
                        case "archive":
                            onClickArchive(constructorFiles.get(position).name);
                            break;
                        default:
                            MainActivity.ToastMakeText(context, context.getString(R.string.warning_file));
                            break;
                    }
                }
            });
            recyclerView.setAdapter(adapterFile);
            HorizontalScrollView horizontalScrollView = rootView.findViewById(R.id.scrollFile);
            horizontalScrollView.post(() ->
                    horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
            );

        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void reload() {
        linearFolders.removeAllViews();
        constructorFiles.clear();
        onStart();
    }

    public void onClickTitleFolder(TextView view) {
        try {
            currentPath = new File(currentPath.getAbsoluteFile().toString().split(view.getText().toString())[0] + view.getText().toString());
            reload();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onClickFolder(TextView view) {
        try {
            currentPath = new File(currentPath.getAbsoluteFile().toString() + "/" + view.getText().toString());
            reload();
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

    public void onClickArchive(String name) {
        try {
            String path = currentPath.getAbsolutePath();
            if(path.substring(path.length() - 1).equals("/"))
                path += name;
            else path += "/" + name;
            ((MainActivity) context).onPatch(path);
        } catch (Exception error) {
            ((MainActivity) context).errorStack(error);
        }
    }

}
