package com.kedzie.vbox.machine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class ScreenshotDialogFragment extends SherlockDialogFragment {
	public static final String BUNDLE_BYTES = "bytes";
	
	private View _view;
	private Bitmap _bitmap;
	
	public static ScreenshotDialogFragment getInstance(Bundle args) {
		ScreenshotDialogFragment f = new ScreenshotDialogFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("Screenshot");
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		byte[] bytes = getArguments().getByteArray(BUNDLE_BYTES);
		_bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.screenshot_dialog, null);
		ImageView imageView = (ImageView)_view.findViewById(R.id.imageView);
		imageView.setImageBitmap(_bitmap);
		((ImageButton)_view.findViewById(R.id.button_save)).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				new SaveScreenshotTask().execute(_bitmap);
			}
		});
		((ImageButton)_view.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) { 
				dismiss(); 
			} 
		});
		return _view;
	}
	
	class SaveScreenshotTask extends ActionBarTask<Bitmap, Void> {

	    private String filename;
	    
		public SaveScreenshotTask() {
			super("SaveScreenshotTask", getSherlockActivity(), null);
			filename =  "screenshot_"+DateFormat.format("yyyyMMdd_hmmssaa", new Date())+".jpg";
		}
		
		@Override
		protected Void work(Bitmap... params) throws Exception {
			File file = new File(Environment.getExternalStorageDirectory().toString(), filename);
			OutputStream fOut = new FileOutputStream(file);
			params[0].compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
			try {
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            } catch (Exception e) {
                Log.e(TAG, "Exception storing in MediaStore", e);
            }
			return null;
		}
	}
}
