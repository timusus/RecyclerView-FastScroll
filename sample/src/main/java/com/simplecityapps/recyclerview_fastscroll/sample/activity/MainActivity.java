/*
 * Copyright (c) 2016 Tim Malseed
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.simplecityapps.recyclerview_fastscroll.sample.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.simplecityapps.recyclerview_fastscroll.sample.R;
import com.simplecityapps.recyclerview_fastscroll.sample.fragment.MultiViewTypeFragment;
import com.simplecityapps.recyclerview_fastscroll.sample.fragment.SimpleFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }
    private static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
            implements FastScrollRecyclerView.SectionedAdapter,
            FastScrollRecyclerView.MeasurableAdapter {

        private static final int REGULAR_ITEM = 0;
        private static final int TALL_ITEM = 1;
        private View view;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @LayoutRes int viewId;

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view,null);
            if (viewType == TALL_ITEM) {
                viewId = R.layout.tall_item;
            } else {
                viewId = R.layout.item;
            }
    private static class PagerAdapter extends FragmentPagerAdapter {


        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SimpleFragment();
                case 1:
                    return new MultiViewTypeFragment();
            }
            throw new IllegalArgumentException(String.format("No fragment returned for position: %d", position));
        }

        @Override
        public int getItemCount() {
            return 200;
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            return " ";
        }

        @Nullable
        @Override
        public View getSectionCustomView(int position) {

            final TextView textView = (TextView) view.findViewById(R.id.text1);
            textView.setText(""+position);
            return view;

        public int getCount() {
            return 2;

        }

        @SuppressLint("DefaultLocale")
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("Page %d", position + 1);
        }
    }
}
