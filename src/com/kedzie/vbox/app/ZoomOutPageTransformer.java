package com.kedzie.vbox.app;

import com.nineoldandroids.view.animation.AnimatorProxy;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class ZoomOutPageTransformer implements PageTransformer {
    private static float MIN_SCALE = 0.85f;
    private static float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        
        AnimatorProxy proxy = AnimatorProxy.wrap(view);

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            proxy.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                proxy.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                proxy.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            proxy.setScaleX(scaleFactor);
            proxy.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            proxy.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            proxy.setAlpha(0);
        }
    }
}
