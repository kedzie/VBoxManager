package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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
    private static final String TAG = "SliderView";
    
    public static interface OnSliderViewChangeListener {
        public void onSliderValueChanged(int newValue);
    }
    
    public class ValidRangeIndicatorView extends View {
        
        private Rect _bounds;
        private Rect _invalidRect;
        private Paint _validPaint;
        private Paint _invalidPaint;
        private Paint _tickPaint;
        /** How many pixels in a single unit */
        private int _pixelsPerUnit;

        public ValidRangeIndicatorView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        
        public ValidRangeIndicatorView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            _validPaint = new Paint();
            _validPaint.setColor(0xFF00FF00);
            _validPaint.setStyle(Style.FILL);
            _invalidPaint = new Paint();
            _invalidPaint.setColor(0xFFFF0000);
            _invalidPaint.setStyle(Style.FILL);
            _tickPaint = new Paint();
            _tickPaint.setColor(0xFF000000);
            _tickPaint.setStyle(Style.STROKE);
            _tickPaint.setStrokeWidth(2f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            Log.i(TAG, String.format("onSizeChanged(%1$d, %2$d)", w, h));
            _bounds = new Rect(getLeft(), getTop(), getRight(), getBottom());
            _pixelsPerUnit = w/_range;
            int invalidLeft = (_minValidValue-_minValue)*_pixelsPerUnit+getLeft();
            int invalidRight = getRight()-(_maxValue-_maxValidValue)*_pixelsPerUnit;
            _invalidRect = new Rect(invalidLeft, getTop(), invalidRight, getBottom());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawRect(_bounds, _validPaint);
            canvas.drawRect(_invalidRect, _invalidPaint);
            for(int x=0; x<=getWidth(); x+=_pixelsPerUnit) 
                canvas.drawLine(x, getBottom(), x,getTop(), _tickPaint);
        }
    }

    /** tick-mark spacing */
    int _tickSpacing;
    /** Unit for slider values */
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
    
    private OnSliderViewChangeListener _onSliderChangeListener;
    
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
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _valueEditText.setText(progress+_minValue+"");
                if(_onSliderChangeListener!=null)
                    _onSliderChangeListener.onSliderValueChanged(progress+_minValue);
            }
        });
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
    
    public void setOnSliderViewChangeListener(OnSliderViewChangeListener listener) {
        _onSliderChangeListener = listener;
    }
}
