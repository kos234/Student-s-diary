package com.example.kos.constructors;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ConstructorDnewnik implements Parcelable {
    public final String nameDay;
    public List<String> strings;
    public final boolean isUmply;


    public ConstructorDnewnik(String nameDay, List<String> strings, boolean isUmply) {
        this.nameDay = nameDay;
        this.strings = strings;
        this.isUmply = isUmply;
    }


    protected ConstructorDnewnik(Parcel in) {
        nameDay = in.readString();
        in.readStringList(strings);
        isUmply = in.readByte() != 0;
    }

    public static final Creator<ConstructorDnewnik> CREATOR = new Creator<ConstructorDnewnik>() {
        @Override
        public ConstructorDnewnik createFromParcel(Parcel in) {
            return new ConstructorDnewnik(in);
        }

        @Override
        public ConstructorDnewnik[] newArray(int size) {
            return new ConstructorDnewnik[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameDay);
        dest.writeStringList(strings);
        dest.writeByte((byte) (isUmply ? 1 : 0));//Я не виноват что они запили метод writeBoolean только в 10 android
    }
}