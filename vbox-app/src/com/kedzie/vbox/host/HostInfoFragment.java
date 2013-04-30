package com.kedzie.vbox.host;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType;
import com.kedzie.vbox.api.jaxb.ProcessorFeature;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * 
 * @apiviz.stereotype fragment
 */
public class HostInfoFragment extends SherlockFragment {
    private static final String TAG = "InfoFragment";

    class LoadInfoTask extends ActionBarTask<IHost, IHost> {

        public LoadInfoTask() { 
            super(getSherlockActivity(), HostInfoFragment.this._vmgr); 
        }

        @Override 
        protected IHost work(final IHost... h) throws Exception {
            //cache values
            fork(new Runnable() {
                @Override
                public void run() {
                    _vmgr.getVBox().getVersion();
                    h[0].getMemorySize();
                    h[0].getMemoryAvailable();
                    h[0].getOperatingSystem();
                    h[0].getOSVersion();
                    for(IMedium drive : h[0].getDVDDrives()) 
                        Utils.cacheProperties(drive);
                    for(IHostNetworkInterface net : h[0].findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED)) {
                        net.getIPAddress(); net.getIPV6Address(); net.getName(); net.getNetworkName(); net.getNetworkMask(); net.getIPV6NetworkMaskPrefixLength();
                    }
                }
            });
            fork(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<h[0].getProcessorCount(); i++) {
                        h[0].getProcessorDescription(i);
                        h[0].getProcessorSpeed(i);
                    }
                    h[0].getProcessorFeature(ProcessorFeature.HW_VIRT_EX);
                    h[0].getProcessorFeature(ProcessorFeature.LONG_MODE);
                    h[0].getProcessorFeature(ProcessorFeature.PAE);
                }
            });
            join();
            return h[0];
        }

        @Override
        protected void onSuccess(IHost result) {
            _host=result;
            populateViews(result);
        }
    }

    private IHost _host;
    private VBoxSvc _vmgr;
    private View _view;
    private TextView _ostypeText;
    private TextView _vboxText;
    private TextView _memoryText;
    private TextView _processorsText;
    private TextView _networksText;
    private TextView _dvdsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.host_info, null);
        _ostypeText = (TextView)_view.findViewById(R.id.ostype);
        _vboxText = (TextView)_view.findViewById(R.id.vbox);
        _memoryText = (TextView)_view.findViewById(R.id.memory);
        _processorsText = (TextView)_view.findViewById(R.id.processors);
        _networksText = (TextView)_view.findViewById(R.id.networks);
        _processorsText = (TextView)_view.findViewById(R.id.processors);
        _dvdsText = (TextView)_view.findViewById(R.id.dvds);
        return _view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        if(savedInstanceState!=null) {
            _host = savedInstanceState.getParcelable(IHost.BUNDLE);
            populateViews(_host);
        } else {
            _host = BundleBuilder.getProxy(getArguments(), IHost.BUNDLE, IHost.class);
            new LoadInfoTask().execute(_host);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleBuilder.putProxy(outState, IHost.BUNDLE, _host);
    }

    private void populateViews(IHost host) {
        _ostypeText.setText(host.getOperatingSystem()+"("+host.getOSVersion()+")");
        _vboxText.setText(_vmgr.getVBox().getVersion());
        _memoryText.setText(host.getMemorySize()+" MB");
        
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<host.getProcessorCount(); i++) {
            if(i>=0)
                sb.append("\n");
            sb.append(host.getProcessorDescription(i));
        }
        StringBuffer sb2 = new StringBuffer();
        if(host.getProcessorFeature(ProcessorFeature.HW_VIRT_EX))
            Utils.appendWithComma(sb2, "HW VirtEx");
        if(host.getProcessorFeature(ProcessorFeature.PAE))
            Utils.appendWithComma(sb2, "PAE");
        if(host.getProcessorFeature(ProcessorFeature.LONG_MODE))
            Utils.appendWithComma(sb2, "Long Mode");
        _processorsText.setText(sb.toString() + "\n" + sb2.toString());
        
        sb = new StringBuffer();
        List<IHostNetworkInterface> nets = host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED);
        for(int i=0; i<nets.size(); i++ ) {
            IHostNetworkInterface net = nets.get(i);
            if(i>=0)
                sb.append("\n\n");
            sb.append(net.getNetworkName());
            sb.append("\n\t").append(net.getIPAddress()).append(" / ").append(net.getNetworkMask());
            sb.append("\n\t").append(net.getIPV6Address()).append(" / ").append(net.getIPV6NetworkMaskPrefixLength());
        }
        _networksText.setText(sb.toString());
        
        sb = new StringBuffer();
        List<IMedium> dvds = host.getDVDDrives();
        for(int i=0; i<dvds.size(); i++ ) {
            IMedium dvd = dvds.get(i);
            if(i>=0)
                sb.append("\n");
            sb.append(dvd.getName()).append(" ").append(dvd.getDescription());
        }
        _dvdsText.setText(sb.toString());
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch(item.getItemId()) {
            case R.id.option_menu_refresh:
                Log.d(TAG, "Refreshing...");
                new LoadInfoTask().execute(_host);
                return true;
        }
        return false;
    }
}