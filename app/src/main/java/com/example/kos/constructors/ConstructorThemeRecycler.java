package com.example.kos.constructors;

import android.os.Parcel;
import android.os.Parcelable;

public class ConstructorThemeRecycler implements Parcelable {
    public String name;
    public final String[] colors;
    public final int ColorBolder;
    public final int ColorIcon;
    public final int IdSwitch;

    public ConstructorThemeRecycler(String[] colors) {
        IdSwitch = Integer.parseInt(colors[0]);
        this.name = colors[1];
        ColorIcon = Integer.parseInt(colors[2]);
        ColorBolder = Integer.parseInt(colors[3]);
        this.colors = colors;
    }

    protected ConstructorThemeRecycler(Parcel in) {
        name = in.readString();
        colors = in.createStringArray();
        ColorBolder = in.readInt();
        ColorIcon = in.readInt();
        IdSwitch = in.readInt();
    }

    public static final Creator<ConstructorThemeRecycler> CREATOR = new Creator<ConstructorThemeRecycler>() {
        @Override
        public ConstructorThemeRecycler createFromParcel(Parcel in) {
            return new ConstructorThemeRecycler(in);
        }

        @Override
        public ConstructorThemeRecycler[] newArray(int size) {
            return new ConstructorThemeRecycler[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringArray(colors);
        dest.writeInt(ColorBolder);
        dest.writeInt(ColorIcon);
        dest.writeInt(IdSwitch);
    }
}