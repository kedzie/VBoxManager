package com.kedzie.vbox.app;

import com.nineoldandroids.view.ViewHelper;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * Performs an accordian animation.
 */
public class AccordianPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        ViewHelper.setTranslationX(view, -1*view.getWidth()*position);
        if(position < 0)
            ViewHelper.setPivotX(view, 0f);
        else if(position > 0)
            ViewHelper.setPivotX(view, view.getWidth());
        ViewHelper.setScaleX(view, 1-Math.abs(position));
    }
}
