package com.example.kos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

class PagerAdapterZvon extends FragmentStatePagerAdapter {
        private String[] name;
    private List<Fragment> list;

        PagerAdapterZvon(@NonNull FragmentManager fm, List<Fragment> list) {
            super(fm);
            name = new String[] {
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота"

            };
            this.list = list;
        }
        @Override
        public CharSequence getPageTitle(int position){
            return name[position];
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
    }

