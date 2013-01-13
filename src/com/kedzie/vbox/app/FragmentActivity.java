package com.kedzie.vbox.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


public class FragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE 
                && Utils.getScreenSize(getResources().getConfiguration())>=Configuration.SCREENLAYOUT_SIZE_LARGE)
            finish();
        
//        FragmentInfo info = (FragmentInfo)getIntent().getParcelableExtra(FragmentInfo.BUNDLE);
        FragmentElement element = (FragmentElement)getIntent().getParcelableExtra(FragmentElement.BUNDLE);
        
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(element.name);
        
        if(savedInstanceState==null) {
//            info.applyFragments(this, android.R.id.content);
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            Fragment fragment = element.instantiate(this);
            tx.add(android.R.id.content, fragment, element.name);
            tx.commit();
        }
    }
}
