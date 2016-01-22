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

package com.simplecityapps.recyclerview_fastscroll.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.simplecityapps.recyclerview_fastscroll.R;
import com.simplecityapps.recyclerview_fastscroll.utils.Utils;

public class FastScroller {

    private FastScrollRecyclerView mRecyclerView;
    private FastScrollPopup mPopup;

    private int mThumbHeight;
    private int mThumbWidth;

    private Paint mThumb;
    private Paint mTrack;

    private Rect mTmpRect = new Rect();
    private Rect mInvalidateRect = new Rect();

    // The inset is the buffer around which a point will still register as a click on the scrollbar
    private int mTouchInset;

    // This is the offset from the top of the scrollbar when the user first starts touching.  To
    // prevent jumping, this offset is applied as the user scrolls.
    private int mTouchOffset;

    public Point mThumbOffset = new Point(-1, -1);

    private boolean mIsDragging;

    public FastScroller(Context context, FastScrollRecyclerView recyclerView, AttributeSet attrs) {

        Resources resources = context.getResources();

        mRecyclerView = recyclerView;
        mPopup = new FastScrollPopup(resources, recyclerView);

        mThumbHeight = Utils.toPixels(resources, 48);
        mThumbWidth = Utils.toPixels(resources, 8);

        mTouchInset = Utils.toPixels(resources, -24);

        mThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrack = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FastScrollRecyclerView, 0, 0);
        try {
            int trackColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollTrackColor, 0x1f000000);
            int thumbColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollThumbColor, 0xff000000);
            int popupBgColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollPopupBgColor, 0xff000000);
            int popupTextColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollPopupTextColor, 0xffffffff);

            mTrack.setColor(trackColor);
            mThumb.setColor(thumbColor);
            mPopup.setBgColor(popupBgColor);
            mPopup.setTextColor(popupTextColor);
        } finally {
            typedArray.recycle();
        }
    }

    public int getHeight() {
        return mThumbHeight;
    }

    public int getWidth() {
        return mThumbWidth;
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    /**
     * Handles the touch event and determines whether to show the fast scroller (or updates it if
     * it is already showing).
     */
    public void handleTouchEvent(MotionEvent ev, int downX, int downY, int lastY) {
        ViewConfiguration config = ViewConfiguration.get(mRecyclerView.getContext());

        int action = ev.getAction();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isNearPoint(downX, downY)) {
                    mTouchOffset = downY - mThumbOffset.y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // Check if we should start scrolling
                if (!mIsDragging && isNearPoint(downX, downY) &&
                        Math.abs(y - downY) > config.getScaledTouchSlop()) {
                    mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                    mIsDragging = true;
                    mTouchOffset += (lastY - downY);
                    mPopup.animateVisibility(true);
                }
                if (mIsDragging) {
                    // Update the fastscroller section name at this touch position
                    int top = 0;
                    int bottom = mRecyclerView.getHeight() - mThumbHeight;
                    float boundedY = (float) Math.max(top, Math.min(bottom, y - mTouchOffset));
                    String sectionName = mRecyclerView.scrollToPositionAtProgress((boundedY - top) / (bottom - top));
                    mPopup.setSectionName(sectionName);
                    mPopup.animateVisibility(!sectionName.isEmpty());
                    mRecyclerView.invalidate(mPopup.updateFastScrollerBounds(mRecyclerView, mThumbOffset.y));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchOffset = 0;
                if (mIsDragging) {
                    mIsDragging = false;
                    mPopup.animateVisibility(false);
                }
                break;
        }
    }

    public void draw(Canvas canvas) {

        if (mThumbOffset.x < 0 || mThumbOffset.y < 0) {
            return;
        }

        //Background
        canvas.drawRect(mThumbOffset.x, mThumbHeight / 2, mThumbOffset.x + mThumbWidth, mRecyclerView.getHeight() - mThumbHeight / 2, mTrack);

        //Handle
        canvas.drawRect(mThumbOffset.x, mThumbOffset.y, mThumbOffset.x + mThumbWidth, mThumbOffset.y + mThumbHeight, mThumb);

        //Popup
        mPopup.draw(canvas);
    }

    /**
     * Returns whether the specified points are near the scroll bar bounds.
     */
    private boolean isNearPoint(int x, int y) {
        mTmpRect.set(mThumbOffset.x, mThumbOffset.y, mThumbOffset.x + mThumbWidth,
                mThumbOffset.y + mThumbHeight);
        mTmpRect.inset(mTouchInset, mTouchInset);
        return mTmpRect.contains(x, y);
    }

    public void setScrollbarThumbOffset(int x, int y) {
        if (mThumbOffset.x == x && mThumbOffset.y == y) {
            return;
        }
        mInvalidateRect.set(mThumbOffset.x, 0, mThumbOffset.x + mThumbWidth, mRecyclerView.getHeight());
        mThumbOffset.set(x, y);
        mInvalidateRect.union(new Rect(mThumbOffset.x, 0, mThumbOffset.x + mThumbWidth, mRecyclerView.getHeight()));
        mRecyclerView.invalidate(mInvalidateRect);
    }

    public void setThumbColor(@ColorInt int color) {
        mThumb.setColor(color);
        mRecyclerView.invalidate(mInvalidateRect);
    }

    public void setTrackColor(@ColorInt int color) {
        mTrack.setColor(color);
        mRecyclerView.invalidate(mInvalidateRect);
    }

    public void setPopupBgColor(@ColorInt int color) {
        mPopup.setBgColor(color);
    }

    public void setPopupTextColor(@ColorInt int color) {
        mPopup.setTextColor(color);
    }
}
