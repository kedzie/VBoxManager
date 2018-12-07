package com.kedzie.vbox.app;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class FlipPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
		view.setTranslationX(-1*view.getWidth()*position);
        if(position >= -.5 && position <= .5) {
            view.setAlpha(1);
            view.setScaleX(1);
		} else {
            view.setAlpha(0);
            view.setScaleX(0);
		}
        view.setRotationY( position*180);
    }
}
