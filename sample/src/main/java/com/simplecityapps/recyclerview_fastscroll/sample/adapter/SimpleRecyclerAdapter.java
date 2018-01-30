package com.simplecityapps.recyclerview_fastscroll.sample.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.simplecityapps.recyclerview_fastscroll.sample.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>
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
        if (position % 2 == 0) {
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

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
