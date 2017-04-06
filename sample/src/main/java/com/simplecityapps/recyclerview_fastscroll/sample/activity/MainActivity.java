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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.sample.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FastScrollRecyclerView recyclerView = (FastScrollRecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerAdapter());
    }

    private static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
            implements FastScrollRecyclerView.SectionedAdapter,
            FastScrollRecyclerView.MeasurableAdapter {

        private static final int REGULAR_ITEM = 0;
        private static final int TALL_ITEM = 1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @LayoutRes int viewId;

            if (viewType == TALL_ITEM) {
                viewId = R.layout.tall_item;
            } else {
                viewId = R.layout.item;
            }

            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            if (position > 100) {
                return TALL_ITEM;
            }
            return REGULAR_ITEM;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.format("Item %d", position));
        }

        @Override
        public int getItemCount() {
            return 200;
        }

        @NonNull
        @Override
        public String getSectionName(int position) {
            return String.valueOf(position);
        }

        @Override
        public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
            if (viewType == REGULAR_ITEM) {
                return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_height);
            } else if (viewType == TALL_ITEM) {
                return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_tall_height);
            }
            return 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
