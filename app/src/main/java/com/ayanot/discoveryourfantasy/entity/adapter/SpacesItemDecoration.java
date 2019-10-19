package com.ayanot.discoveryourfantasy.entity.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpaceVertical;
    private final int mSpaceHorizontal;

    public SpacesItemDecoration(int mSpaceVertical, int mSpaceHorizontal) {
        this.mSpaceVertical = mSpaceVertical;
        this.mSpaceHorizontal = mSpaceHorizontal;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = mSpaceVertical;
        outRect.right = mSpaceVertical;
        outRect.bottom = mSpaceHorizontal;
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = mSpaceHorizontal;
    }
}
