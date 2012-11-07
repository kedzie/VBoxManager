package com.kedzie.vbox.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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

    /** tick-mark spacing */
    private int _tickSpacing;
    private String _unit;
    private int _minValue;
    private int _maxValue;
    private int _minValidValue;
    private int _maxValidValue;
    
    private SeekBar _seekBar;
    private TextView _minValueLabel;
    private TextView _maxValueLabel;
    private EditText _valueEditText;
    
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
        setOrientation(VERTICAL);
        _seekBar = new SeekBar(getContext());
        _seekBar.setThumb(getContext().getResources().getDrawable(R.drawable.slider_thumb));
        _seekBar.setMax(_maxValue-_minValue);
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
        _minValueLabel = new TextView(getContext());
        _minValueLabel.setText(_minValue+_unit);
        _maxValueLabel = new TextView(getContext());
        _maxValueLabel.setText(_maxValue+_unit);
        _valueEditText = new EditText(getContext());
        
        LinearLayout top = new LinearLayout(getContext());
        top.setOrientation(HORIZONTAL);
        addView(top, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        LinearLayout bottom = new LinearLayout(getContext());
        bottom.setOrientation(HORIZONTAL);
        addView(bottom, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight=1;
        top.addView(_seekBar, params);
        
        top.addView(_valueEditText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        bottom.addView(_minValueLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight=1;
        bottom.addView(new View(getContext()), params);
        bottom.addView(_maxValueLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public int getValue() {
        return _seekBar.getProgress()+_minValue;
    }
}
