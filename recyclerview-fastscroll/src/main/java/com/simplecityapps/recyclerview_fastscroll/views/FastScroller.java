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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.simplecityapps.recyclerview_fastscroll.R;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.utils.Utils;

import java.lang.annotation.Retention;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("WeakerAccess")
public class FastScroller {
    private static final int DEFAULT_AUTO_HIDE_DELAY = 1500;

    private FastScrollRecyclerView mRecyclerView;
    private FastScrollPopup mPopup;

    private int mThumbHeight;
    private int mThumbWidth;
    private Paint mThumb;

    private Paint mTrack;
    private int mTrackWidth;

    private Rect mTmpRect = new Rect();
    private Rect mInvalidateRect = new Rect();
    private Rect mInvalidateTmpRect = new Rect();

    // The inset is the buffer around which a point will still register as a click on the scrollbar
    private int mTouchInset;

    // This is the offset from the top of the scrollbar when the user first starts touching.  To
    // prevent jumping, this offset is applied as the user scrolls.
    private int mTouchOffset;

    private Point mThumbPosition = new Point(-1, -1);
    private Point mOffset = new Point(0, 0);

    private boolean mIsDragging;

    private Animator mAutoHideAnimator;
    private boolean mAnimatingShow;
    private int mAutoHideDelay = DEFAULT_AUTO_HIDE_DELAY;
    private boolean mAutoHideEnabled = true;
    private final Runnable mHideRunnable;

    private int mThumbActiveColor;
    private int mThumbInactiveColor = 0x79000000;
    private boolean mThumbInactiveState;

    private int mTouchSlop;

    private int mLastY;

    @Retention(SOURCE)
    @IntDef({PopupTextVerticalAlignmentMode.TEXT_BOUNDS, PopupTextVerticalAlignmentMode.FONT_METRICS})
    public @interface PopupTextVerticalAlignmentMode {
        int TEXT_BOUNDS = 0;
        int FONT_METRICS = 1;
    }
    @IntDef({PopupPosition.ADJACENT, PopupPosition.CENTER})
    public @interface PopupPosition {
        int ADJACENT = 0;
        int CENTER = 1;
    }

    public FastScroller(Context context, FastScrollRecyclerView recyclerView, AttributeSet attrs) {

        Resources resources = context.getResources();

        mRecyclerView = recyclerView;
        mPopup = new FastScrollPopup(resources, recyclerView);

        mThumbHeight = Utils.toPixels(resources, 52);
        mThumbWidth = Utils.toPixels(resources, 8);
        mTrackWidth = Utils.toPixels(resources, 6);

        mTouchInset = Utils.toPixels(resources, -24);

        mThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrack = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FastScrollRecyclerView, 0, 0);
        try {
            mAutoHideEnabled = typedArray.getBoolean(R.styleable.FastScrollRecyclerView_fastScrollAutoHide, true);
            mAutoHideDelay = typedArray.getInteger(R.styleable.FastScrollRecyclerView_fastScrollAutoHideDelay, DEFAULT_AUTO_HIDE_DELAY);
            mThumbInactiveState = typedArray.getBoolean(R.styleable.FastScrollRecyclerView_fastScrollEnableThumbInactiveColor, true);
            mThumbActiveColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollThumbColor, 0x79000000);
            mThumbInactiveColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollThumbInactiveColor, 0x79000000);

            int trackColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollTrackColor, 0x28000000);
            int popupBgColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollPopupBgColor, 0xff000000);
            int popupTextColor = typedArray.getColor(R.styleable.FastScrollRecyclerView_fastScrollPopupTextColor, 0xffffffff);
            int popupTextSize = typedArray.getDimensionPixelSize(R.styleable.FastScrollRecyclerView_fastScrollPopupTextSize, Utils.toScreenPixels(resources, 32));
            int popupBackgroundSize = typedArray.getDimensionPixelSize(R.styleable.FastScrollRecyclerView_fastScrollPopupBackgroundSize, Utils.toPixels(resources, 62));
            @PopupTextVerticalAlignmentMode int popupTextVerticalAlignmentMode = typedArray.getInteger(R.styleable.FastScrollRecyclerView_fastScrollPopupTextVerticalAlignmentMode, PopupTextVerticalAlignmentMode.TEXT_BOUNDS);
            @PopupPosition int popupPosition = typedArray.getInteger(R.styleable.FastScrollRecyclerView_fastScrollPopupPosition, PopupPosition.ADJACENT);

            mThumbWidth =
                typedArray.getDimensionPixelSize(R.styleable.FastScrollRecyclerView_fastScrollThumbWidth, mThumbWidth);
            mTrackWidth =
                typedArray.getDimensionPixelSize(R.styleable.FastScrollRecyclerView_fastScrollTrackWidth, mTrackWidth);

            mTrack.setColor(trackColor);
            mThumb.setColor(mThumbInactiveState ? mThumbInactiveColor : mThumbActiveColor);
            mPopup.setBgColor(popupBgColor);
            mPopup.setTextColor(popupTextColor);
            mPopup.setTextSize(popupTextSize);
            mPopup.setBackgroundSize(popupBackgroundSize);
            mPopup.setPopupTextVerticalAlignmentMode(popupTextVerticalAlignmentMode);
            mPopup.setPopupPosition(popupPosition);
        } finally {
            typedArray.recycle();
        }

        mHideRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsDragging) {
                    if (mAutoHideAnimator != null) {
                        mAutoHideAnimator.cancel();
                    }
                    mAutoHideAnimator = ObjectAnimator.ofInt(FastScroller.this, "offsetX", (Utils.isRtl(mRecyclerView.getResources()) ? -1 : 1) * getWidth());
                    mAutoHideAnimator.setInterpolator(new FastOutLinearInInterpolator());
                    mAutoHideAnimator.setDuration(200);
                    mAutoHideAnimator.start();
                }
            }
        };

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!mRecyclerView.isInEditMode()) {
                    show();
                }
            }
        });

        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        }
    }

    public int getThumbHeight() {
        return mThumbHeight;
    }

    public int getWidth() {
        return Math.max(mTrackWidth, mThumbWidth);
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    /**
     * Handles the touch event and determines whether to show the fast scroller (or updates it if
     * it is already showing).
     */
    public void handleTouchEvent(MotionEvent ev, int downX, int downY, int lastY,
                                 OnFastScrollStateChangeListener stateChangeListener) {
        int action = ev.getAction();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isNearPoint(downX, downY)) {
                    mTouchOffset = downY - mThumbPosition.y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // Check if we should start scrolling
                if (!mIsDragging && isNearPoint(downX, downY) &&
                        Math.abs(y - downY) > mTouchSlop) {
                    mRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                    mIsDragging = true;
                    mTouchOffset += (lastY - downY);
                    mPopup.animateVisibility(true);
                    if (stateChangeListener != null) {
                        stateChangeListener.onFastScrollStart();
                    }
                    if (mThumbInactiveState) {
                        mThumb.setColor(mThumbActiveColor);
                    }
                }
                if (mIsDragging) {
                    if (mLastY == 0 || Math.abs(mLastY - y) >= mTouchSlop) {
                        mLastY = y;
                        // Update the fastscroller section name at this touch position
                        boolean layoutManagerReversed = mRecyclerView.isLayoutManagerReversed();
                        int bottom = mRecyclerView.getHeight() - mThumbHeight;
                        float boundedY = (float) Math.max(0, Math.min(bottom, y - mTouchOffset));

                        // Represents the amount the thumb has scrolled divided by its total scroll range
                        float touchFraction = boundedY / bottom;
                        if (layoutManagerReversed) {
                            touchFraction = 1 - touchFraction;
                        }

                        String sectionName = mRecyclerView.scrollToPositionAtProgress(touchFraction);
                        mPopup.setSectionName(sectionName);
                        mPopup.animateVisibility(!sectionName.isEmpty());
                        mRecyclerView.invalidate(mPopup.updateFastScrollerBounds(mRecyclerView, mThumbPosition.y));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchOffset = 0;
                mLastY = 0;
                if (mIsDragging) {
                    mIsDragging = false;
                    mPopup.animateVisibility(false);
                    if (stateChangeListener != null) {
                        stateChangeListener.onFastScrollStop();
                    }
                }
                if (mThumbInactiveState) {
                    mThumb.setColor(mThumbInactiveColor);
                }
                break;
        }
    }

    RectF rect = new RectF();

    public void draw(Canvas canvas) {

        if (mThumbPosition.x < 0 || mThumbPosition.y < 0) {
            return;
        }

        //Background
        rect.set(mThumbPosition.x + mOffset.x + (mThumbWidth - mTrackWidth),
                mOffset.y + mRecyclerView.getPaddingTop(),
                mThumbPosition.x + mOffset.x + mTrackWidth + (mThumbWidth - mTrackWidth),
                mRecyclerView.getHeight() + mOffset.y - mRecyclerView.getPaddingBottom());
        canvas.drawRoundRect(rect,
                mTrackWidth,
                mTrackWidth,
                mTrack);

        //Handle
        rect.set(mThumbPosition.x + mOffset.x + (mThumbWidth - mTrackWidth) / 2,
                mThumbPosition.y + mOffset.y,
                mThumbPosition.x + mOffset.x + mThumbWidth + (mThumbWidth - mTrackWidth) / 2,
                mThumbPosition.y + mOffset.y + mThumbHeight);
        canvas.drawRoundRect(rect,
                mThumbWidth,
                mThumbWidth,
                mThumb);

        //Popup
        mPopup.draw(canvas);

    }

    /**
     * Returns whether the specified points are near the scroll bar bounds.
     */
    private boolean isNearPoint(int x, int y) {
        mTmpRect.set(mThumbPosition.x, mThumbPosition.y, mThumbPosition.x + mTrackWidth,
                mThumbPosition.y + mThumbHeight);
        mTmpRect.inset(mTouchInset, mTouchInset);
        return mTmpRect.contains(x, y);
    }

    public void setThumbPosition(int x, int y) {
        if (mThumbPosition.x == x && mThumbPosition.y == y) {
            return;
        }
        // do not create new objects here, this is called quite often
        mInvalidateRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mTrackWidth, mRecyclerView.getHeight() + mOffset.y);
        mThumbPosition.set(x, y);
        mInvalidateTmpRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mTrackWidth, mRecyclerView.getHeight() + mOffset.y);
        mInvalidateRect.union(mInvalidateTmpRect);
        mRecyclerView.invalidate(mInvalidateRect);
    }


    public void setOffset(int x, int y) {
        if (mOffset.x == x && mOffset.y == y) {
            return;
        }
        // do not create new objects here, this is called quite often
        mInvalidateRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mTrackWidth, mRecyclerView.getHeight() + mOffset.y);
        mOffset.set(x, y);
        mInvalidateTmpRect.set(mThumbPosition.x + mOffset.x, mOffset.y, mThumbPosition.x + mOffset.x + mTrackWidth, mRecyclerView.getHeight() + mOffset.y);
        mInvalidateRect.union(mInvalidateTmpRect);
        mRecyclerView.invalidate(mInvalidateRect);
    }

    // Setter/getter for the popup alpha for animations
    @Keep
    public void setOffsetX(int x) {
        setOffset(x, mOffset.y);
    }

    @Keep
    public int getOffsetX() {
        return mOffset.x;
    }

    public void show() {
        if (!mAnimatingShow) {
            if (mAutoHideAnimator != null) {
                mAutoHideAnimator.cancel();
            }
            mAutoHideAnimator = ObjectAnimator.ofInt(this, "offsetX", 0);
            mAutoHideAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            mAutoHideAnimator.setDuration(150);
            mAutoHideAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mAnimatingShow = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mAnimatingShow = false;
                }
            });
            mAnimatingShow = true;
            mAutoHideAnimator.start();
        }
        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        } else {
            cancelAutoHide();
        }
    }

    protected void postAutoHideDelayed() {
        if (mRecyclerView != null) {
            cancelAutoHide();
            mRecyclerView.postDelayed(mHideRunnable, mAutoHideDelay);
        }
    }

    protected void cancelAutoHide() {
        if (mRecyclerView != null) {
            mRecyclerView.removeCallbacks(mHideRunnable);
        }
    }

    public void setThumbColor(@ColorInt int color) {
        mThumbActiveColor = color;
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

    public void setPopupTypeface(Typeface typeface) {
        mPopup.setTypeface(typeface);
    }

    public void setPopupTextSize(int size) {
        mPopup.setTextSize(size);
    }

    public void setAutoHideDelay(int hideDelay) {
        mAutoHideDelay = hideDelay;
        if (mAutoHideEnabled) {
            postAutoHideDelayed();
        }
    }

    public void setAutoHideEnabled(boolean autoHideEnabled) {
        mAutoHideEnabled = autoHideEnabled;
        if (autoHideEnabled) {
            postAutoHideDelayed();
        } else {
            cancelAutoHide();
        }
    }

    public void setPopupPosition(@PopupPosition int popupPosition) {
        mPopup.setPopupPosition(popupPosition);
    }

    public void setThumbInactiveColor(@ColorInt int color) {
        mThumbInactiveColor = color;
        enableThumbInactiveColor(true);
    }

    public void enableThumbInactiveColor(boolean enableInactiveColor) {
        mThumbInactiveState = enableInactiveColor;
        mThumb.setColor(mThumbInactiveState ? mThumbInactiveColor : mThumbActiveColor);
    }

    @Deprecated
    public void setThumbInactiveColor(boolean thumbInactiveColor) {
        enableThumbInactiveColor(thumbInactiveColor);
    }
}
