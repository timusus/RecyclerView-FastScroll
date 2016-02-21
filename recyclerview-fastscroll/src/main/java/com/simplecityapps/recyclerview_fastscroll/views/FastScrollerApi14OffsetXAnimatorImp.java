package com.simplecityapps.recyclerview_fastscroll.views;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;

/**
 * Created by nammari on 2/22/16.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class FastScrollerApi14OffsetXAnimatorImp implements FastScrollerOffsetXAnimator {

    private static final Property<FastScroller, Integer> OFFSETX_PROPERTY =
            new Property<FastScroller, Integer>(Integer.class, "offsetX") {
                @Override
                public Integer get(FastScroller fastScroller) {
                    return fastScroller.getOffsetX();
                }

                @Override
                public void set(FastScroller fastScroller, Integer offsetX) {
                    fastScroller.setOffsetX(offsetX);
                }
            };

    @Override
    public ObjectAnimator getOffsetXAnimator(FastScroller target, int... value) {
        return ObjectAnimator.ofInt(target, OFFSETX_PROPERTY, value);

    }
}
