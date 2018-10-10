package com.simplecityapps.recyclerview_fastscroll.sample.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.sample.R;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class MultiViewTypeAdapter extends RecyclerView.Adapter<MultiViewTypeAdapter.ViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 4 == 0) {
            return R.layout.list_item_header;
        }
        return R.layout.list_item;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(getNameForItem(position));
    }

    @Override
    public int getItemCount() {
        return 200;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String getSectionName(int position) {
        if (position % 4 == 0) {
            return String.format("H%d", (position / 4) + 1);
        } else {
            return String.format("%d", position - position / 4);
        }
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
        if (position % 4 == 0) {
            return String.format("Header %d", (position / 4) + 1);
        } else {
            return String.format("Item %d", position - position / 4);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
