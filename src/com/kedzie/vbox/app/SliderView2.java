package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kedzie.vbox.R;

public class SliderView2 extends View {
    private static final String TAG = "SliderView2";
    
    /** tick-mark spacing */
    private int _tickSpacing;
    private String _unit;
    private int _minValue;
    private int _maxValue;
    private int _minValidValue;
    private int _maxValidValue;
    
    private Paint background = new Paint();

    public SliderView2(Context context) {
        super(context);
    }
    
    public SliderView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public SliderView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MetricView, 0, 0);
        try {
            _tickSpacing = a.getInt(R.styleable.SliderView_tickSpacing, 1);
            _unit = a.getString(R.styleable.SliderView_unit);
            _minValue = a.getInt(R.styleable.SliderView_minValue, 1);
            _maxValue = a.getInt(R.styleable.SliderView_maxValue, 10);
            _minValidValue = a.getInt(R.styleable.SliderView_minValidValue, 1);
            _maxValidValue = a.getInt(R.styleable.SliderView_maxValidValue, 10);
        } finally {
            a.recycle();
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        
        switch(wMode) {
            case MeasureSpec.AT_MOST:
                Log.i(TAG, "Width:  At Most " + width);
                break;
            case MeasureSpec.EXACTLY:
                Log.i(TAG, "Height:  Exactly " + width);
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.i(TAG, "Height:  Unspecified");
                break;
        }
        switch(hMode) {
            case MeasureSpec.AT_MOST:
                Log.i(TAG, "Height:  At Most " + height);
                break;
            case MeasureSpec.EXACTLY:
                Log.i(TAG, "Height:  Exactly " + height);
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.i(TAG, "Height:  Unspecified");
                break;
        }
//        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = canvas.getClipBounds();
        background.setColor(0xFF0000FF);
        background.setStyle(Style.FILL);
        canvas.drawRect(bounds, background);
    }

}
