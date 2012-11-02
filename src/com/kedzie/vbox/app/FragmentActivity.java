package com.kedzie.vbox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


public class FragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utils.isLargeLand(getResources().getConfiguration()))
            finish();
        
        TabFragmentInfo info = (TabFragmentInfo)getIntent().getParcelableExtra(TabFragmentInfo.BUNDLE);
        
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(info.name);
        
        if(savedInstanceState==null) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            Fragment fragment = info.instantiate(this);
            tx.add(android.R.id.content, fragment, info.name);
            tx.commit();
        }
    }
}
