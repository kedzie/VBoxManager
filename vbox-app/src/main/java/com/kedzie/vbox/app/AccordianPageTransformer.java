package com.kedzie.vbox.app;

import androidx.viewpager.widget.ViewPager.PageTransformer;
import android.view.View;

/**
 * Performs an accordian animation.
 */
public class AccordianPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(-1*view.getWidth()*position);
        if(position < 0)
            view.setPivotX(0f);
        else if(position > 0)
            view.setPivotX(view.getWidth());
        view.setScaleX(1-Math.abs(position));
    }
}
