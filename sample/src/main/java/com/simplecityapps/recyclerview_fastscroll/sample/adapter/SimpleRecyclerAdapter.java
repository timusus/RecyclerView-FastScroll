package com.simplecityapps.recyclerview_fastscroll.sample.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_item;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(getNameForItem(position));
    }

    @Override
    public int getItemCount() {
        return 12000;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.format("%d", position + 1);
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView,
                                 @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        if (viewType == R.layout.list_item_header) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_header_height);
        } else if (viewType == R.layout.list_item) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.list_item_height);
        }
        return 0;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    private String getNameForItem(int position) {
        return String.format("Item %d", position + 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
