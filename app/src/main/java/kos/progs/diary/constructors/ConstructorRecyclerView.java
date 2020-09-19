package kos.progs.diary.constructors;

import android.os.Parcel;
import android.os.Parcelable;

public class ConstructorRecyclerView implements Parcelable {

    public String TextName;
    public String TextBottom;

    public ConstructorRecyclerView(String textName, String textBottom) {
        TextName = textName;
        TextBottom = textBottom;
    }

    protected ConstructorRecyclerView(Parcel in) {
        TextName = in.readString();
        TextBottom = in.readString();
    }

    public static final Creator<ConstructorRecyclerView> CREATOR = new Creator<ConstructorRecyclerView>() {
        @Override
        public ConstructorRecyclerView createFromParcel(Parcel in) {
            return new ConstructorRecyclerView(in);
        }

        @Override
        public ConstructorRecyclerView[] newArray(int size) {
            return new ConstructorRecyclerView[size];
        }
    };

    public void changeText(String textNameEdit, String textBottomEdit) {
        TextName = textNameEdit;
        TextBottom = textBottomEdit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TextName);
        dest.writeString(TextBottom);
    }
}