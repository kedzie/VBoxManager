package com.kedzie.vbox.app;

import com.actionbarsherlock.view.MenuItem;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;


public class FragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE 
                && Utils.getScreenSize(getResources().getConfiguration())>=Configuration.SCREENLAYOUT_SIZE_LARGE) {
            finish();
        }
        
        FragmentElement element = (FragmentElement)getIntent().getParcelableExtra(FragmentElement.BUNDLE);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(element.name);
        if(element.icon!=-1)
        	getSupportActionBar().setIcon(element.icon);
        
        if(savedInstanceState==null) {
        	Utils.replaceFragment(this, getSupportFragmentManager(), android.R.id.content, element);
        }
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return false;
	}

    @Override
    public void finish() {
        super.finish();
        Utils.overrideBackTransition(this);
    }
}
