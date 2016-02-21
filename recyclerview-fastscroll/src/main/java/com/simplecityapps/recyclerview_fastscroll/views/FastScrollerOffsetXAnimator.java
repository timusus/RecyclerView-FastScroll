package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;

/**
 * Created by nammari on 2/22/16.
 */
interface FastScrollerOffsetXAnimator {

    ObjectAnimator getOffsetXAnimator(FastScroller target, int... value);
}
