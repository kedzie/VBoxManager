package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
	
    public static interface OnSliderViewChangeListener {
        public void onSliderValidValueChanged(int newValue);
        public void onSliderInvalidValueChanged(int newValue);
    }
    
    public class SliderBar extends SeekBar {
    	private final int VALID_RANGE_BAR_HEIGHT=Utils.dpiToPx(getContext(), 12);
    	
    	private Rect _invalidRect;
        private Rect _validRect;
        private Paint _validPaint;
        private Paint _invalidPaint;
        private Paint _tickPaint;
        private Paint _textPaint;
        /** How many pixels in a single unit */
        private float _pixelsPerUnit;
        
        private Drawable mThumb;
        
        public SliderBar(Context context) {
            super(context);
            setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    _valueEditText.setText(progress+_minValue+"");
                    if(_onSliderChangeListener!=null) {
                    	int value = progress+_minValue;
                    	if(value >= _minValidValue && value <= _maxValidValue)
                    		_onSliderChangeListener.onSliderValidValueChanged(value);
                    	else
                    		_onSliderChangeListener.onSliderInvalidValueChanged(value);
                    }
                }
            });
            mThumb=getContext().getResources().getDrawable(R.drawable.ic_navigation_expand);
            setThumb(mThumb);
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
            _textPaint = new Paint();
            _textPaint.setColor(getResources().getColor(android.R.color.primary_text_dark));
            _textPaint.setTextSize(Utils.dpiToPx(getContext(), 12));
        }
        
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            updateProperties();
        }
        
        void updateProperties() {
        	_pixelsPerUnit = ((float)getWidth())/(_maxValue-_minValue);
            int validLeft = (int) ((_minValidValue-_minValue)*_pixelsPerUnit+getLeft()+getPaddingLeft());
            int validRight =(int) (getRight()-getPaddingRight()-(_maxValue-_maxValidValue)*_pixelsPerUnit);
            int bottom = getBottom()-getPaddingBottom()-(int)_textPaint.getTextSize();
            int top = bottom-VALID_RANGE_BAR_HEIGHT;
            _invalidRect = new Rect(getLeft()+getPaddingLeft(), top, getRight()-getPaddingRight(), bottom);
            _validRect = new Rect(validLeft, top, validRight, bottom);
            postInvalidate();
        }
        
        @Override
        protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Drawable d = super.getProgressDrawable();
            int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
            int dw = 0;
            int dh = 0;
            if (d != null) {
                dw =d.getIntrinsicWidth();
                dh = Math.max(thumbHeight, d.getIntrinsicHeight());
            }
            dw += getPaddingLeft() + getPaddingRight();
            dh += getPaddingTop() + getPaddingBottom() + VALID_RANGE_BAR_HEIGHT+_textPaint.getTextSize();
            setMeasuredDimension(resolveSize(dw, widthMeasureSpec), resolveSize(dh, heightMeasureSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
        	
            canvas.drawRect(_invalidRect, _invalidPaint);
            canvas.drawRect(_validRect, _validPaint);
            for(int x=_invalidRect.left; x<=_invalidRect.right; x+=_pixelsPerUnit*_tickSpacing) 
                canvas.drawLine(x, _invalidRect.bottom, x, _invalidRect.top, _tickPaint);
            
            String min = _minValue + "  " + _unit;
            Rect minBounds = new Rect();
            _textPaint.getTextBounds(min, 0, min.length(), minBounds);
            canvas.drawText(min, getLeft()+getPaddingLeft(), getBottom(), _textPaint);
            
            String max = _maxValue + " " + _unit;
            Rect maxBounds = new Rect();
            _textPaint.getTextBounds(max, 0, max.length(), maxBounds);
            canvas.drawText(max, getRight()-getPaddingRight()-maxBounds.width(), getBottom(), _textPaint);
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
    
    private SliderBar _sliderBar;
    private TextView _unitLabel;
    private EditText _valueEditText;
    private OnSliderViewChangeListener _onSliderChangeListener;
    
    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0);
        try {
            _tickSpacing = a.getInt(R.styleable.SliderView_tickSpacing, 1);
            _unit = a.hasValue(R.styleable.SliderView_unit) ?  a.getString(R.styleable.SliderView_unit) : "NULL";
            _minValue = a.getInt(R.styleable.SliderView_minValue, 1);
            _maxValue = a.getInt(R.styleable.SliderView_maxValue, 10);
            _minValidValue = a.getInt(R.styleable.SliderView_minValidValue, 4);
            _maxValidValue = a.getInt(R.styleable.SliderView_maxValidValue, 8);
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
        setOrientation(HORIZONTAL);
        
      _sliderBar = new SliderBar(getContext());
      addView(_sliderBar, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        
        _valueEditText = new EditText(getContext());
        _valueEditText.setText(getValue()+"");
        addView(_valueEditText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        _unitLabel = new TextView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 2;
        addView(_unitLabel, lp);
        
        updateProperties();
        _unitLabel.setText(_unit);
    }
    
    private void updateProperties() {
    	_range=_maxValue-_minValue;
    	_sliderBar.setMax(_range);
    	_sliderBar.updateProperties();
    }

    public void setOnSliderViewChangeListener(OnSliderViewChangeListener listener) {
        _onSliderChangeListener = listener;
    }
    
    public int getValue() {
        return _sliderBar.getProgress()+_minValue;
    }
    
    public void setValue(int value) {
    	_sliderBar.setProgress(value-_minValue);
    }
    
    public int getMaxValue() {
    	return _maxValue;
    }
    
    public void setMaxValue(int max) {
    	_maxValue=max;
    	updateProperties();
    }
    
    public int getMinValue() {
    	return _minValue;
    }
    
    public void setMinValue(int min) {
    	_minValue=min;
    	updateProperties();
    }
    
    public int getMaxValidValue() {
    	return _maxValidValue;
    }
    
    public void setMaxValidValue(int max) {
    	_maxValidValue=max;
    	updateProperties();
    }
    
    public int getMinValidValue() {
    	return _minValidValue;
    }
    
    public void setMinValidValue(int min) {
    	_minValidValue=min;
    	updateProperties();
    }
}
