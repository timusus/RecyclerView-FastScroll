package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;

/**
 * Created by nammari on 2/22/16.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class FastScrollPopupApi14AlphaAnimatorImp implements FastScrollPopupAlphaAnimator {


    private static final Property<FastScrollPopup, Float> ALPHA_PROPERTY =
            new Property<FastScrollPopup, Float>(Float.class, "alpha") {
                @Override
                public Float get(FastScrollPopup fastScrollPopup) {
                    return fastScrollPopup.getAlpha();
                }

                @Override
                public void set(FastScrollPopup fastScrollPopup, Float alpha) {
                    fastScrollPopup.setAlpha(alpha);
                }
            };

    @Override
    public ObjectAnimator getAlphaAnimator(FastScrollPopup target, float... values) {
        return ObjectAnimator.ofFloat(target, ALPHA_PROPERTY, values);
    }
}
