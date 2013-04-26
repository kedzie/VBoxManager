package com.kedzie.vbox.app;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.nineoldandroids.view.animation.AnimatorProxy;

public class FlipPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        AnimatorProxy proxy = AnimatorProxy.wrap(view);
        
        proxy.setTranslationX(-1 *  view.getWidth() * position);
        
        if(position >= -.5 && position <= .5)
            proxy.setAlpha(1);
        else
            proxy.setAlpha(0);
        proxy.setRotationY(position*-180);
    }

}
