package com.kedzie.vbox.machine.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentActivity;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.settings.CategoryFragment.OnSelectCategoryListener;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Obtain a write-lock and then edit virtual machine settings
 * 
 * @apiviz.stereotype activity
 */
public class VMSettingsActivity extends BaseActivity implements OnSelectCategoryListener, HasSupportFragmentInjector {
    private static final String TAG = "VMSettingsActivity";
    public static final String MUTABLE_KEY = "mutable";

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }

    /**
     * Get Write Lock on machine 
     */
    class LockMachineTask extends ActionBarTask<IMachine, IMachine> {
    	
        public LockMachineTask() { 
        	super(VMSettingsActivity.this, VMSettingsActivity.this._vmgr);
        
        }
        @Override 
        protected IMachine work(IMachine... m) throws Exception {
            ISession session = _vmgr.getVBox().getSessionObject();
            m[0].lockMachine(session, LockType.WRITE);
            return session.getMachine();
        }
        
        @Override
        protected void onSuccess(IMachine result) {
            super.onSuccess(result);
            _mutable = result;
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.add(R.id.list, Fragment.instantiate(VMSettingsActivity.this, CategoryFragment.class.getName(), new BundleBuilder().putProxy(IMachine.BUNDLE, _mutable).create()));
            tx.commit();
        }
    }
    
    /**
     * Save settings
     */
    class SaveSettingsTask extends ActionBarTask<IMachine, Integer> {
    	
        public SaveSettingsTask() {
        	super(VMSettingsActivity.this, VMSettingsActivity.this._vmgr);
        }

        @Override 
        protected Integer work(IMachine... m) throws Exception {
            m[0].saveSettings();
            _vmgr.getVBox().getSessionObject().unlockMachine();
            return 1;
        }
        
        @Override
        protected void onSuccess(Integer result) {
            super.onSuccess(result);
            Utils.toastLong(VMSettingsActivity.this, VMSettingsActivity.this.getString(R.string.toast_saved_settings));
            finish();
        }
    }
    
    /**
     * Discard settings
     */
    class DiscardSettingsTask extends ActionBarTask<IMachine, Integer> {
    	
        public DiscardSettingsTask() { 
        	super(VMSettingsActivity.this, VMSettingsActivity.this._vmgr);
        }
        
        @Override 
        protected Integer work(IMachine... m) throws Exception {
            m[0].discardSettings();
            _vmgr.getVBox().getSessionObject().unlockMachine();
            return 1;
        }
        @Override
        protected void onSuccess(Integer result) {
            super.onSuccess(result);
            Utils.toastLong(VMSettingsActivity.this, getString(R.string.toast_discarding_settings));
            finish();
        }
    }

    /** Fragment Manager */
    private FragmentManager _mgr;
	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;

	/** VirtualBox API */
	private VBoxSvc _vmgr;
	
	/** Immutable vm reference */
	private IMachine _machine;
	
	/** Mutable vm reference.  Initialized when machine is successfully WRITE-Locked */
	private IMachine _mutable;
	
	/** Currently selected settings category */
	private String currentCategory;
    /** Currently selected category index */
    private int currentPosition=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_vmgr = BundleBuilder.getVBoxSvc(getIntent());
		_machine = BundleBuilder.getProxy(getIntent(), IMachine.BUNDLE, IMachine.class);

        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(_machine.getName() + " Settings");
		getSupportActionBar().setIcon(getApp().getOSDrawable(_machine.getOSTypeId()));

        _mgr = getSupportFragmentManager();
		setContentView(R.layout.fragment_list_layout);
        _dualPane = findViewById(R.id.details) != null;
		
		if(savedInstanceState==null) 
		    new LockMachineTask().execute(_machine);
		else {
	        _mutable = savedInstanceState.getParcelable(MUTABLE_KEY);
		}
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(Utils.getScreenSize(newConfig)==Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Log.i(TAG, "Handling orientation change");

            Fragment list = _mgr.findFragmentById(R.id.list);
            _mgr.beginTransaction().detach(list).commit();

            Fragment details = _mgr.findFragmentById(R.id.details);
            if(details!=null) {
//                _mgr.beginTransaction().remove(details).commit();
            }

            setContentView(R.layout.fragment_list_layout);
            _dualPane = findViewById(R.id.details)!=null;
            _mgr.beginTransaction().attach(list).commit();
        }
    }

    @Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    if(_mutable!=null)
	        BundleBuilder.putProxy(outState, MUTABLE_KEY, _mutable);
	}

	@Override
    public void onSelectCategory(int position, FragmentElement category) {
	    if(_dualPane) {
	        FragmentTransaction tx = _mgr.beginTransaction();
            if(currentPosition==-1) {
                tx.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
            } else if(position>currentPosition) {
                tx.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom);
            } else {
                tx.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_top);
            }
	        Utils.detachFragment(_mgr, tx, R.id.details);
            Utils.addOrAttachFragment(this, _mgr, tx, R.id.details, category);
            tx.commit();
	    } else {
	        Utils.startActivity(this, new Intent(this, FragmentActivity.class).putExtra(FragmentElement.BUNDLE, category));
	    }
	    currentCategory = category.name;
        currentPosition = position;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.machine_settings, menu);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_save:
			saveSettings();
			return true;
		case R.id.option_menu_discard:
		    discardSettings();
		    return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return false;
	}

	@Override 
	public void onBackPressed() {
		Fragment frag = _mgr.findFragmentById(R.id.details);
		if(frag!=null && frag.getChildFragmentManager().popBackStackImmediate())
			return;

		new AlertDialog.Builder(this)
	        .setTitle("Save Changes?")
	        .setMessage("Do you wish to save changes?")
	        .setIcon(android.R.drawable.ic_dialog_info)
	        .setPositiveButton("OK", new OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                saveSettings();
	                finish();
	            }
	        })
	        .setNegativeButton("Discard", new OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	                discardSettings();
	                finish();
	            }
	        })
	        .show();
	}
	
	private void saveSettings() {
		new SaveSettingsTask().execute(_mutable);
	}
	
	private void discardSettings() {
		if(_mutable!=null)
			new DiscardSettingsTask().execute(_mutable);
	}
	
	@Override
    public void finish() {
        super.finish();
        Utils.overrideBackTransition(this);
    }
}
