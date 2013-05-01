package com.kedzie.vbox.app;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class FlipPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        ViewHelper.setTranslationX(view, -1*view.getWidth()*position);
        if(position >= -.5 && position <= .5)
            ViewHelper.setAlpha(view, 1);
        else
            ViewHelper.setAlpha(view, 0);
        ViewHelper.setRotationY(view, position*-180);
    }
}
