package com.kedzie.vbox.machine.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.TabFragmentInfo;

public class SettingsPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE  && getResources().getConfiguration().smallestScreenWidthDp>=600) 
            finish();
        
        TabFragmentInfo info = (TabFragmentInfo)getIntent().getParcelableExtra("info");
        
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(info.name);
        
        if(savedInstanceState==null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.add(info.instantiate(this), info.name);
            tx.commit();
        }
    }
}
