package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;

/**
 * Created by nammari on 2/22/16.
 */
interface FastScrollPopupAlphaAnimator {

    ObjectAnimator getAlphaAnimator(FastScrollPopup target, float... values);

}
