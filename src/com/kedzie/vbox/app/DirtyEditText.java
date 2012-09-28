package com.kedzie.vbox.app;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Keeps track of whether the value was changed or not.
 * 
 * @author Marek Kędzierski
 */
public class DirtyEditText extends EditText {
	
	private boolean _dirty;
	private TextWatcher _textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			_dirty=true;
		}
		@Override	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override public void afterTextChanged(Editable s) {}
	};
	
	public DirtyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		addTextChangedListener(_textWatcher);
	}

	public DirtyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		addTextChangedListener(_textWatcher);
	}

	public DirtyEditText(Context context) {
		super(context);
		addTextChangedListener(_textWatcher);
	}
	
	public boolean isDirty() {
		return _dirty;
	}

	public void setDirty(boolean dirty) {
		this._dirty = dirty;
	}
}
