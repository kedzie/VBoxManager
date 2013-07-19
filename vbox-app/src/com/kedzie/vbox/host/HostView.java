package com.kedzie.vbox.host;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;

/**
 * Show VM information
 * 
 * @apiviz.stereotype view
 */
public class HostView extends FrameLayout {

	private TextView ipText;
	private TextView versionText;
	private IHost _host;
	
	public HostView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.host_view, this, true);
		ipText =(TextView)findViewById(R.id.host_ip);
		versionText = (TextView)findViewById(R.id.host_version);
	}
	
	public void update(IHost h) {
			_host=h;
			ipText.setText("("+h.getAPI().getServer().getHost()+")");
			versionText.setText(h.getAPI().getVBox().getVersion());
	}

	public IHost getHost() {
	    return _host;
	}
}
