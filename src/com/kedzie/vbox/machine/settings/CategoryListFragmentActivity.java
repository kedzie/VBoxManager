package com.kedzie.vbox.machine.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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

/**
 * 
 * @apiviz.stereotype activity
 */
public class CategoryListFragmentActivity extends BaseActivity implements OnSelectCategoryListener {
    public static final String MUTABLE_KEY = "mutable";

    /**
     * Get Write Lock on machine 
     */
    class LockMachineTask extends ActionBarTask<IMachine, IMachine> {
        public LockMachineTask() { super("LockMachineTask", CategoryListFragmentActivity.this, CategoryListFragmentActivity.this._vmgr); }
        @Override 
        protected IMachine work(IMachine... m) throws Exception {
            ISession session = _vmgr.getVBox().getSessionObject();
            m[0].lockMachine(session, LockType.WRITE);
            return session.getMachine();
        }
        @Override
        protected void onResult(IMachine result) {
            super.onResult(result);
            _mutable = result;
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.add(R.id.list, Fragment.instantiate(CategoryListFragmentActivity.this, CategoryFragment.class.getName(), new BundleBuilder().putProxy(IMachine.BUNDLE, _mutable).create()));
            tx.commit();
        }
    }
    
    /**
     * Save settings
     */
    class SaveSettingsTask extends ActionBarTask<IMachine, Integer> {
        public SaveSettingsTask() { super("SaveSettingsTask", CategoryListFragmentActivity.this, CategoryListFragmentActivity.this._vmgr); }

        @Override 
        protected Integer work(IMachine... m) throws Exception {
            m[0].saveSettings();
            _vmgr.getVBox().getSessionObject().unlockMachine();
            return 1;
        }
        
        @Override
        protected void onResult(Integer result) {
            super.onResult(result);
            Utils.toastLong(_context, "Saved Settings");
            finish();
        }
    }
    
    /**
     * Discard settings
     */
    class DiscardSettingsTask extends ActionBarTask<IMachine, Integer> {
        public DiscardSettingsTask() { super("DiscardSettingsTask", CategoryListFragmentActivity.this, CategoryListFragmentActivity.this._vmgr); }
        @Override 
        protected Integer work(IMachine... m) throws Exception {
            m[0].discardSettings();
            _vmgr.getVBox().getSessionObject().unlockMachine();
            return 1;
        }
        @Override
        protected void onResult(Integer result) {
            super.onResult(result);
            Utils.toastLong(_context, "Discarded Settings");
            finish();
        }
    }
    
	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private IMachine _mutable;
	private String currentCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		_machine = BundleBuilder.getProxy(getIntent(), IMachine.BUNDLE, IMachine.class);
		
		getSupportActionBar().setTitle(_machine.getName() + " Settings");
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		
		setContentView(R.layout.category_list);
		FrameLayout detailsFrame = (FrameLayout)findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility()==View.VISIBLE;
		
		if(savedInstanceState==null) 
		    new LockMachineTask().execute(_machine);
		else {
		    currentCategory = savedInstanceState.getString("currentCategory");
		    if(savedInstanceState.containsKey(MUTABLE_KEY))
		        _mutable = BundleBuilder.getProxy(savedInstanceState, MUTABLE_KEY, IMachine.class);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("currentCategory", currentCategory);
	    if(_mutable!=null)
	        BundleBuilder.putProxy(outState, MUTABLE_KEY, _mutable);
	}
	
	@Override
    public void onSelectCategory(FragmentElement category) {
	    if(_dualPane) {
	        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
	        Utils.detachExistingFragment(getSupportFragmentManager(), tx, currentCategory);
            Utils.addOrAttachFragment(this, getSupportFragmentManager(), tx, R.id.details, category);
            tx.commit();
	    } else {
	        startActivity(new Intent(this, FragmentActivity.class).putExtra(FragmentElement.BUNDLE, category));
	    }
	    currentCategory = category.name;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.machine_settings, menu);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_save:
		    new SaveSettingsTask().execute(_mutable);
			return true;
		case R.id.option_menu_discard:
		    discardSettings();
		    return true;
		}
		return false;
	}

	@Override 
	public void onBackPressed() {
	    discardSettings();
		super.onBackPressed();
	}
	
	private void discardSettings() {
	    new DiscardSettingsTask().execute(_mutable);
	}
}
