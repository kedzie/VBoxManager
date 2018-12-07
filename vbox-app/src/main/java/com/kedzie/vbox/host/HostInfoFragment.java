package com.kedzie.vbox.host;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType;
import com.kedzie.vbox.api.jaxb.ProcessorFeature;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * @apiviz.stereotype fragment
 */
public class HostInfoFragment extends Fragment {
    private static final String TAG = "InfoFragment";

    private static final int REQUEST_CODE_PREFERENCES = 6;

    class LoadInfoTask extends ActionBarTask<IHost, IHost> {

        private boolean clearCache;

        public LoadInfoTask(boolean clearCache) {
            super((AppCompatActivity)getActivity(), HostInfoFragment.this._vmgr);
            this.clearCache = clearCache;
        }

        @Override
        protected IHost work(final IHost... h) throws Exception {
            if(clearCache)
                h[0].clearCache();
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

    @BindView(R.id.ostype)
     TextView _ostypeText;
    @BindView(R.id.vbox)
     TextView _vboxText;
    @BindView(R.id.memory)
     TextView _memoryText;
    @BindView(R.id.processors)
     TextView _processorsText;
    @BindView(R.id.networks)
     TextView _networksText;
    @BindView(R.id.dvds)
     TextView _dvdsText;

    private boolean mDualPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        _vmgr = BundleBuilder.getVBoxSvc(getArguments());
        _host = BundleBuilder.getProxy(getArguments(), IHost.BUNDLE, IHost.class);
        mDualPane = getArguments().getBoolean("dualPane");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.host_info, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null) {
            _vmgr = BundleBuilder.getVBoxSvc(savedInstanceState);
            _host = savedInstanceState.getParcelable(IHost.BUNDLE);
            populateViews(_host);
        } else {
            new LoadInfoTask(false).execute(_host);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleBuilder.putProxy(outState, IHost.BUNDLE, _host);
        BundleBuilder.putVBoxSvc(outState, _vmgr);
    }

    private void populateViews(IHost host) {
        try {
            _ostypeText.setText(host.getOperatingSystem()+"("+host.getOSVersion()+")");
            _vboxText.setText(_vmgr.getVBox().getVersion());
            _memoryText.setText(host.getMemorySize()+" MB");

            StringBuffer sb = new StringBuffer();
            for(int i=0; i<host.getProcessorCount(); i++) {
                if(i>0)
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
            _processorsText.setLines(host.getProcessorCount()+1);
            _processorsText.setText(sb.toString() + "\n" + sb2.toString());

            sb = new StringBuffer();
            List<IHostNetworkInterface> nets = host.findHostNetworkInterfacesOfType(HostNetworkInterfaceType.BRIDGED);
            for(int i=0; i<nets.size(); i++ ) {
                IHostNetworkInterface net = nets.get(i);
                if(i>0)
                    sb.append("\n\n");
                sb.append(net.getNetworkName());
                sb.append("\n\t").append(net.getIPAddress()).append(" / ").append(net.getNetworkMask());
                sb.append("\n\t").append(net.getIPV6Address()).append(" / ").append(net.getIPV6NetworkMaskPrefixLength());
            }
            _networksText.setLines(nets.size()*4-1);
            _networksText.setText(sb.toString());

            sb = new StringBuffer();
            List<IMedium> dvds = host.getDVDDrives();
            for(int i=0; i<dvds.size(); i++ ) {
                IMedium dvd = dvds.get(i);
                if(i>0)
                    sb.append("\n");
                sb.append(dvd.getName()).append(" ").append(dvd.getDescription());
            }
            _dvdsText.setLines(dvds.size());
            _dvdsText.setText(sb.toString());
        } catch(NetworkOnMainThreadException e) {
            new LoadInfoTask(false).execute(host);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.host_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.option_menu_refresh:
                Log.d(TAG, "Refreshing...");
                new LoadInfoTask(true).execute(_host);
                return false;
            case R.id.host_option_menu_metrics:
                startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vmgr)
                        .putExtra(MetricActivity.INTENT_TITLE, getResources().getString(R.string.host_metrics))
                        .putExtra(MetricActivity.INTENT_ICON, R.drawable.ic_launcher)
                        .putExtra(MetricActivity.INTENT_OBJECT, _vmgr.getVBox().getHost().getIdRef() )
                        .putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _vmgr.getVBox().getHost().getMemorySize())
                        .putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
                        .putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
                return true;
        }
        return false;
    }
}