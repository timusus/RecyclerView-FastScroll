package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;

/**
 * Created by nammari on 2/22/16.
 */
class FastScrollerPreApi14OffsetXAnimatorImp implements FastScrollerOffsetXAnimator {


    @Override
    public ObjectAnimator getOffsetXAnimator(FastScroller target, int... value) {
        return ObjectAnimator.ofInt(target, "offsetX", value);
    }
}
