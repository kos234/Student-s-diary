package kos.progs.diary.constructors;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ConstructorGrades implements Parcelable {
    public final int num;
    public ArrayList<String> grades;
    public String lesionString, gradesString, title;
    public boolean icNotGrades;

    public ConstructorGrades(int num, String lesionString, String gradesString) {
        this.num = num;
        this.lesionString = lesionString;
        this.gradesString = gradesString;
    }

    public ConstructorGrades(int num, ArrayList<String> grades, boolean icNotGrades, String title) {
        this.num = num;
        this.grades = grades;
        this.icNotGrades = icNotGrades;
        this.title = title;
    }

    protected ConstructorGrades(Parcel in) {
        num = in.readInt();
        grades = in.createStringArrayList();
        lesionString = in.readString();
        gradesString = in.readString();
        title = in.readString();
        icNotGrades = in.readByte() != 0;
    }

    public static final Creator<ConstructorGrades> CREATOR = new Creator<ConstructorGrades>() {
        @Override
        public ConstructorGrades createFromParcel(Parcel in) {
            return new ConstructorGrades(in);
        }

        @Override
        public ConstructorGrades[] newArray(int size) {
            return new ConstructorGrades[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(num);
        dest.writeStringList(grades);
        dest.writeString(lesionString);
        dest.writeString(gradesString);
        dest.writeString(title);
        dest.writeByte((byte) (icNotGrades ? 1 : 0));
    }
}
