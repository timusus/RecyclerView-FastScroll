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

package com.simplecity.recyclerview_fastscroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;

/**
 * A simple FastScroller for RecyclerViews.
 * <p/>
 * In xml, place this view as a sibling to your RecyclerView, and set the layout_gravity to 'end'.
 * Also, set the width to 96dp. Setting the width won't be necessary once this View is complete.
 * <p/>
 * Lookup your FastScroller and call {@link #setRecyclerView(RecyclerView)}
 * <p/>
 * Todo: Allow attributes for setting colors
 * Todo: Make the fast scroll movement smoother
 * Todo: Set the width from within the view, rather than via xml
 * Todo: Set the gravity to end from within the view.
 */
public class FastScroller extends LinearLayout {

    private static final String TAG = "FastScroller";

    private RecyclerView mRecyclerView;

    private int mHeight;
    private int mHandleHeight = toPixels(getContext(), 48);
    private int mHandleWidth = toPixels(getContext(), 8);
    private int mOverlayHeight = toPixels(getContext(), 88);
    private int mOverlayWidth = mOverlayHeight;
    private int cornerRadius = mOverlayHeight / 2;

    private Paint mHandle;
    private Paint mTrack;
    private Paint mOverlay;
    private Paint mTextPaint;

    private RectF mOverlayRect;
    private Path mOverlayPath;

    private float mHandleY;
    private float mOverlayY;

    private boolean mShowOverlay;
    private boolean mIsDragging;

    private String mText;

    private AlphaAnimation mAlphaAnimation;
    private Transformation mTransformation;

    private LinearInterpolator mInterpolator;
    private ReverseInterpolator mReverseInterpolator;

    public FastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastScroller(Context context) {
        super(context);
        init(context);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        setWillNotDraw(false);

        //Set the handle color to the accent color
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent, android.R.attr.textColorPrimaryInverse});
        int handleColor = a.getColor(0, 0);
        int textColor = a.getColor(1, 0);
        a.recycle();

        mHandle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandle.setColor(handleColor);

        //Set the track to grey
        int trackColor = 0xffdcdcdc;

        mTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrack.setColor(trackColor);

        mOverlay = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOverlay.setColor(handleColor);
        mOverlay.setAlpha(0);
        mOverlayPath = new Path();
        mOverlayRect = new RectF();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setAlpha(0);
        //Todo: Set typeface from attributes or create method setTypeface()
        //mTextPaint.setTypeface(TypefaceManager.getInstance().getTypeface(TypefaceManager.SANS_SERIF));
        mTextPaint.setTextSize(toPixels(getContext(), 48));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mAlphaAnimation = new AlphaAnimation(0f, 1f);
        mTransformation = new Transformation();
        mAlphaAnimation.setDuration(200);

        mInterpolator = new LinearInterpolator();
        mReverseInterpolator = new ReverseInterpolator();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //If the touch occured outside of 3 handle widths from the edge, ignore it
                if (!mIsDragging) {
                    if (event.getX() < getWidth() - mHandleWidth * 3) {
                        return false;
                    }
                }
                //The user has initiated a touch/drag event. Start moving the list accordingly, and
                //show the overlay
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    mIsDragging = true;
                    setPosition(event.getY());
                    toggleOverlay(true);
                    setRecyclerViewPosition(event.getY());
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //The user has finished dragging, hide the overlay
                    mIsDragging = false;
                    toggleOverlay(false);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Pass in a recyclerView to interact with the FastScroller
     *
     * @param recyclerView the {@link RecyclerView}
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    /**
     * Shows/hides the overlay, with a fade animation
     *
     * @param show true to show the overlay, false otherwise.
     */
    private void toggleOverlay(boolean show) {
        if (mShowOverlay != show) {
            mShowOverlay = show;
            //If the overlay is already showing/hiding when we want to show/hide it, don't do anything
            if ((mShowOverlay && mOverlay.getAlpha() == 255) || (!mShowOverlay && mOverlay.getAlpha() == 0)) {
                return;
            }
            //If an animation is in place, cancel it
            if (mAlphaAnimation.hasStarted() && !mAlphaAnimation.hasEnded()) {
                mAlphaAnimation.cancel();
            }
            //Start the animation
            mAlphaAnimation.setInterpolator(show ? mInterpolator : mReverseInterpolator);
            mAlphaAnimation.start();
            mAlphaAnimation.getTransformation(System.currentTimeMillis(), mTransformation);
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Background
        canvas.drawRect(canvas.getWidth() - mHandleWidth, mHandleHeight / 2, canvas.getWidth(), canvas.getHeight() - mHandleHeight / 2, mTrack);

        //Handle
        canvas.drawRect(canvas.getWidth() - mHandleWidth, mHandleY, canvas.getWidth(), mHandleY + mHandleHeight, mHandle);

        //Overlay
        if (!TextUtils.isEmpty(mText)) {
            mOverlayRect.set(canvas.getWidth() - mOverlayWidth - mHandleWidth, (int) mOverlayY, canvas.getWidth() - mHandleWidth, mOverlayY + mOverlayHeight);
            mOverlayPath.reset();
            mOverlayPath.addRoundRect(mOverlayRect, new float[]{
                    cornerRadius, cornerRadius,
                    cornerRadius, cornerRadius,
                    0, 0,
                    cornerRadius, cornerRadius
            }, Path.Direction.CW);
            canvas.drawPath(mOverlayPath, mOverlay);

            //Text
            canvas.drawText(mText, mOverlayWidth / 2, mOverlayY + mOverlayHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
        }

        if (mAlphaAnimation.hasStarted() && !mAlphaAnimation.hasEnded()) {
            mAlphaAnimation.getTransformation(System.currentTimeMillis(), mTransformation);
            //Keep drawing until we are done
            mOverlay.setAlpha((int) (255 * mTransformation.getAlpha()));
            mTextPaint.setAlpha((int) (255 * mTransformation.getAlpha()));
            invalidate();
        }
    }

    /**
     * Scrolls the RecyclerView to the nearest position according to the ratio y
     *
     * @param y the ratio (current position / item count)
     */
    private void setRecyclerViewPosition(float y) {
        if (mRecyclerView != null) {
            int itemCount = mRecyclerView.getAdapter().getItemCount();
            float ratio;
            if (mHandleY == 0) {
                ratio = 0f;
            } else if (mHandleY + mHandleHeight >= mHeight) {
                ratio = 1f;
            } else {
                ratio = y / (float) (mHeight - mHandleHeight);
            }
            int pos = clamp((int) (ratio * (float) itemCount), 0, itemCount - 1);

            mRecyclerView.scrollToPosition(pos);

            setText(pos);
        }
    }

    /**
     * Set the position of the handle/overlay according to the ratio y
     *
     * @param y the ratio (current position / item count)
     */
    private void setPosition(float y) {
        //Todo: Check y: Is it a ratio, or just a position?
        float position = y / mHeight;

        //Set the handle position
        mHandleY = clamp((int) ((mHeight - mHandleHeight) * position), 0, mHeight - mHandleHeight);

        //Now set the overlay position
        if (y < (mOverlayHeight - (mHandleHeight / 2))) {
            mOverlayY = 0;
        } else {
            mOverlayY = clamp((int) ((mHeight - mHandleHeight) * position) - (mOverlayHeight - (mHandleHeight / 2)), 0, mHeight - mHandleHeight);
        }
        invalidate();
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!mIsDragging) {

                //The total number of items in the RecyclerView
                int itemCount = mRecyclerView.getAdapter().getItemCount();

                //The position of the first visible item
                int firstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                //The position of the last visible item
                int lastVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                //The number of visible children
                int visibleRange = lastVisiblePosition - firstVisiblePosition + 1;

                //The ratio of 'scrolledness' of the RecyclerView
                float ratio = (float) firstVisiblePosition / (itemCount - visibleRange);

                setPosition(mHeight * ratio);
            }
        }
    };

    /**
     * Retrieves the text from the SectionIndexer, according to the position passed in
     *
     * @param position the current item position
     */
    private void setText(int position) {
        if (mRecyclerView.getAdapter() instanceof SectionIndexer) {
            SectionIndexer sectionIndexer = (SectionIndexer) mRecyclerView.getAdapter();
            if (sectionIndexer.getSections().length != 0 && position >= 0) {
                int section = sectionIndexer.getSectionForPosition(position);
                if (section >= 0) {
                    Object object = sectionIndexer.getSections()[section];
                    if (object != null) {
                        String text = object.toString();
                        if (text != null) {
                            if (mText == null || !mText.equals(text)) {
                                mText = text;
                                invalidate();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Return a value limited to the given range
     *
     * @param value the value
     * @param min   the lower boundary
     * @param max   the upper boundary
     * @return int
     */
    private int clamp(int value, int min, int max) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    /**
     * Converts dp to px
     *
     * @param context context
     * @param dp      the value in dp
     * @return int
     */
    public int toPixels(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * A simple interpolator used for reversing an animation
     */
    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

}
