package com.example.kos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

class PagerAdapterZvon extends FragmentStatePagerAdapter {
        private String[] name;

        PagerAdapterZvon(@NonNull FragmentManager fm, Context context) {
            super(fm);
            name = new String[] {
                    context.getString(R.string.monday),
                    context.getString(R.string.tuesday),
                    context.getString(R.string.wednesday),
                    context.getString(R.string.thursday),
                    context.getString(R.string.friday),
                    context.getString(R.string.saturday)
            };

        }
        @Override
        public CharSequence getPageTitle(int position){
            return name[position];
        }
        @Override
        public int getCount() {
            return name.length;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ItemFragment("Monday.txt");

                    case 1:
                    return new ItemFragment("Tuesday.txt");
                case 2:
                    return new ItemFragment("Wednesday.txt");
                case 3:
                    return new ItemFragment("Thursday.txt");
                    case 4:
                    return new ItemFragment("Friday.txt");
                case 5:
                    return new ItemFragment("Saturday.txt");
            }
            return null;
        }
    }

