package com.kedzie.vbox.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;


public class FragmentActivity extends BaseActivity {

	private String TAG = "FragmentActivity";

    public static final String KEY_PARENT_ACTIVITY = "parent";

	private FragmentElement mFragment;
    private String mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent = getIntent().getStringExtra(KEY_PARENT_ACTIVITY);
        mFragment = getIntent().getParcelableExtra(FragmentElement.BUNDLE);
        TAG = mFragment.name;

        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE 
                && Utils.getScreenSize(getResources().getConfiguration())>=Configuration.SCREENLAYOUT_SIZE_LARGE) {
            if(mParent!=null) {
                Utils.overrideBackTransition(this);
                NavUtils.navigateUpTo(this, new Intent().setComponent(new ComponentName(this, mParent)).putExtras(mFragment.args));
            } else
                finish();
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE| ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setTitle(mFragment.name);
        if(mFragment.icon!=-1)
        	getSupportActionBar().setIcon(mFragment.icon);
        
        if(savedInstanceState==null) {
        	Utils.replaceFragment(this, getSupportFragmentManager(), android.R.id.content, mFragment);
        }
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
                if(mParent!=null) {
                    Utils.overrideBackTransition(this);
                    NavUtils.navigateUpTo(this, new Intent().setComponent(new ComponentName(this, mParent)).putExtras(getIntent()));
                } else
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

	@Override
	public void onBackPressed() {
		Fragment frag = getSupportFragmentManager().findFragmentById(android.R.id.content);
		if(frag==null || !frag.getChildFragmentManager().popBackStackImmediate())
			super.onBackPressed();
	}
}
