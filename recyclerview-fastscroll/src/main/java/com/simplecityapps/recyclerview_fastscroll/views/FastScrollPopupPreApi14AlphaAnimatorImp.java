package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;

/**
 * Created by nammari on 2/22/16.
 */
class FastScrollPopupPreApi14AlphaAnimatorImp implements FastScrollPopupAlphaAnimator {

    @Override
    public ObjectAnimator getAlphaAnimator(FastScrollPopup target, float... values) {
        return ObjectAnimator.ofFloat(target, "alpha", values);
    }
}
