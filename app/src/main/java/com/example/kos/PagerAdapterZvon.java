package com.example.kos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class PagerAdapterZvon extends FragmentPagerAdapter {
        private String[] name;
        PagerAdapterZvon(@NonNull FragmentManager fm) {
            super(fm);
            name = new String[] {
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота"

            };
        }
        @Override
        public CharSequence getPageTitle(int position){
            return name[position];
        }
        @Override
        public int getCount() {
            return 6;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new classMonday();
                case 1:
                    return new classTuesday();
                case 2:
                    return new classWednesday();
                case 3:
                    return new classThursday();
                case 4:
                    return new classFriday();
                case 5:
                    return new classSaturday();

                default:
                    return new classMonday();
            }
        }
    }

