package com.kedzie.vbox.machine.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.IStorageController;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.IMediumAttachment;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentActivity;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.settings.StorageListFragment.OnMediumAttachmentClickedListener;
import com.kedzie.vbox.machine.settings.StorageListFragment.OnStorageControllerClickedListener;

public class StorageFragment extends SherlockFragment implements OnStorageControllerClickedListener, OnMediumAttachmentClickedListener {

	private View _view;
    private boolean _dualPane;
    private StorageListFragment _listFragment;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    _view = LayoutInflater.from(getActivity()).inflate(R.layout.settings_storage, null);
        FrameLayout detailsFrame = (FrameLayout)_view.findViewById(R.id.details);
        _dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        
        Fragment f = getChildFragmentManager().findFragmentByTag("list");
        if(f==null) {
            _listFragment = new StorageListFragment();
            _listFragment.setArguments(getArguments());
            getChildFragmentManager().beginTransaction().add(R.id.list, _listFragment, "list").commit();
        } else {
            _listFragment = (StorageListFragment)f;
        }
        return _view;
	}

    @Override
    public void onStorageControllerClicked(IStorageController element) {
        show(new FragmentElement(element.getName(), R.drawable.ic_settings_storage, StorageControllerFragment.class, new BundleBuilder().putAll(getArguments()).putParcelable(IStorageController.BUNDLE, element).create()));
    }
    
    @Override
	public void onMediumAttachmentClicked(IMediumAttachment element) {
    	FragmentElement details = new FragmentElement("Attachment", R.drawable.ic_settings_storage, null, new BundleBuilder().putAll(getArguments()).putParcelable(IMedium.BUNDLE, element).create());
    	if(element.getType().equals(DeviceType.HARD_DISK))
    		details.clazz = StorageHardDiskFragment.class;
    	else if(element.getType().equals(DeviceType.DVD))
    		details.clazz = StorageDVDFragment.class;
//    	else if(element.getType().equals(DeviceType.FLOPPY))
//    		details.clazz = StorageFloppyFragment.class;
        show(details);
	}
    
    private void show(FragmentElement details) {
    	if(_dualPane) {
    		Utils.setCustomAnimations(getChildFragmentManager().beginTransaction()).replace(R.id.details, details.instantiate(getActivity())).commit();
        } else {
        	startActivity(new Intent(getActivity(), FragmentActivity.class).putExtra(FragmentElement.BUNDLE, details));
        }
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(_listFragment !=null)
            _listFragment.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(_listFragment != null)
             return _listFragment.onOptionsItemSelected(item);
        return false;
    }
}
