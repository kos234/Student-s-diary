package kos.progs.diary.constructors;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

public class ConstructorFragmentViewPager implements Parcelable {

    public final ArrayList<ConstructorRecyclerView> products;
    public final String url;
    public View view;

    public ConstructorFragmentViewPager(ArrayList<ConstructorRecyclerView> products, String url) {
        this.products = products;
        this.url = url;
    }

    protected ConstructorFragmentViewPager(Parcel in) {
        url = in.readString();
        products = (ArrayList<ConstructorRecyclerView>) in.readValue(ArrayList.class.getClassLoader());
    }

    public static final Creator<ConstructorFragmentViewPager> CREATOR = new Creator<ConstructorFragmentViewPager>() {
        @Override
        public ConstructorFragmentViewPager createFromParcel(Parcel in) {
            return new ConstructorFragmentViewPager(in);
        }

        @Override
        public ConstructorFragmentViewPager[] newArray(int size) {
            return new ConstructorFragmentViewPager[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeValue(products);
    }
}