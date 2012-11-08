package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kedzie.vbox.R;

/**
 * Feature-rich Horizontal Slider Form component.  
 * <ul>
 * <li>Tick marks along axis</li>
 * <li>Textual display of Min/Max/Current Values</li>
 * <li>Optionally insert value directly using EditText</li>
 * </ul>
 */
public class SliderView extends LinearLayout {
    
    public static class ValidRangeIndicatorView extends View {
        
        private Rect _bounds = new Rect();
        private Rect _validRect;
        private Rect _invalidRect;
        private Paint validPaint;
        private Paint invalidPaint;

        public ValidRangeIndicatorView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        
        public ValidRangeIndicatorView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            validPaint = new Paint();
            validPaint.setColor(0xFF00FF00);
            validPaint.setStyle(Style.FILL);
            invalidPaint = new Paint();
            invalidPaint.setColor(0xFFFF0000);
            invalidPaint.setStyle(Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.getClipBounds(_bounds);
            _validRect = _bounds;
            canvas.drawRect(_validRect, validPaint);
        }
    }

    /** tick-mark spacing */
    int _tickSpacing;
    private String _unit;
    int _minValue;
    int _maxValue;
    int _minValidValue;
    int _maxValidValue;
    int _range;
    
    private SeekBar _seekBar;
    private TextView _minValueLabel;
    private TextView _maxValueLabel;
    private EditText _valueEditText;
    private ValidRangeIndicatorView _validRangeIndicator;
    
    public SliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public SliderView(Context context, AttributeSet attrs, int defStyle) {
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
        init();
    }

    public SliderView(Context context) {
        super(context);
        init();
    }

    private void init() {
        _range=_maxValue-_minValue;
        LayoutInflater.from(getContext()).inflate(R.layout.slider_view, this, true);
        _seekBar = (SeekBar)findViewById(R.id.seekbar);
        _seekBar.setMax(_range);
        _seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _valueEditText.setText(progress+_minValue+"");
            }
        });
        _validRangeIndicator = (ValidRangeIndicatorView)findViewById(R.id.validRangeIndicator);
        _minValueLabel = (TextView)findViewById(R.id.minValueLabel);
        _minValueLabel.setText(_minValue+" "+_unit);
        _maxValueLabel = (TextView)findViewById(R.id.maxValueLabel);
        _maxValueLabel.setText(_maxValue+" "+_unit);
        _valueEditText = (EditText)findViewById(R.id.editText);
        _valueEditText.setText(getValue()+"");
    }

    public int getValue() {
        return _seekBar.getProgress()+_minValue;
    }
}
