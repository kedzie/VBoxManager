package com.kedzie.vbox;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.KSOAPTransport;
import com.kedzie.vbox.api.SOAPInvocationHandler;


public class VBoxMonitorActivity extends Activity {
	protected static final String TAG = VBoxMonitorActivity.class.getSimpleName();
	private static final String NAMESPACE = "http://www.virtualbox.org/";
	
	private String URL = "http://192.168.1.10:18083";
	private KSOAPTransport transport;
	private String vboxID;
	
	private ProgressDialog pDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i(TAG,"Created");
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		new LoadMachinesTask().execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
		SoapObject request = new SoapObject(NAMESPACE, "IWebsessionManager_logoff");
		request.addProperty("refIVirtualBox", vboxID);
		try {
			transport.call(request);
		} catch (Exception e) {
			Log.e(TAG, "error: "+request.getName(), e);
		} 
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "create context menu");
	}
	
	public void showProgress() {
		pDialog = new ProgressDialog(VBoxMonitorActivity.this);
		pDialog.setIndeterminate(true);
		pDialog.show();
	}
	
	public void dismissProgress() {
		this.pDialog.dismiss();
	}

	private class LoadMachinesTask extends AsyncTask<Void, Void, List<IMachine>>
	{
		@Override
		protected void onPreExecute()		{
			showProgress();
			pDialog.setMessage("Connecting to" + URL);
		}

		@Override
		protected List<IMachine> doInBackground(Void... params)	{
			try	{
				transport = new KSOAPTransport(URL);
				
				SoapObject request = new SoapObject(NAMESPACE, "IWebsessionManager_logon");
				request.addProperty("username", "");
				request.addProperty("password", "");
				vboxID =  transport.callString(request);
				
				IVirtualBox vbox = (IVirtualBox)Proxy.newProxyInstance(VBoxMonitorActivity.class.getClassLoader(), new Class [] { IVirtualBox.class }, new SOAPInvocationHandler(vboxID, "IVirtualBox", URL));
				Log.i(TAG, "Logged in.  VBox version: " + vbox.getVersion());
				
				Object ret = vbox.getMachines();
				List<IMachine> machines = new ArrayList<IMachine>();
				for(SoapPrimitive p : (Vector<SoapPrimitive>)ret)
					machines.add((IMachine)Proxy.newProxyInstance(VBoxMonitorActivity.class.getClassLoader(), new Class [] { IMachine.class }, new SOAPInvocationHandler(p.toString(), "IMachine", URL)));
				return machines;
			}
			catch(Exception e)	{
				e.printStackTrace();
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final List<IMachine> result)	{
			ListView listView = (ListView)findViewById(R.id.machines_list);
			listView.setAdapter(new MachinesListAdapter(VBoxMonitorActivity.this, result));
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.i(TAG, "machine clicked: " + result.get(position).getName());
					new launchVMProcessTask().execute(result.get(position).getId());
				}
			});
			dismissProgress();
		}
	}
	
	private class launchVMProcessTask extends AsyncTask<String, Void, List<IMachine>> {
		@Override
		protected void onPreExecute()		{
			showProgress();
			pDialog.setMessage("Launching machine");
		}

		@Override
		protected List<IMachine> doInBackground(String... params)	{
			try	{
				transport = new KSOAPTransport(URL);
				
				SoapObject request = new SoapObject(NAMESPACE, "IWebsessionManager_getSessionObject");
				request.addProperty("refIVirtualBox", vboxID);
				String session = transport.callString(request);
				
				request = new SoapObject(NAMESPACE, "IMachine_launchVMProcess");
				request.addProperty("_this", params[0]);
				request.addProperty("session", session);
				request.addProperty("type", "headless");
				String progress = transport.callString(request);
				
				IVirtualBox vbox = (IVirtualBox)Proxy.newProxyInstance(VBoxMonitorActivity.class.getClassLoader(), new Class [] { IVirtualBox.class }, new SOAPInvocationHandler(vboxID, "IVirtualBox", URL));
				
				Object ret = vbox.getMachines();
				List<IMachine> machines = new ArrayList<IMachine>();
				for(SoapPrimitive p : (Vector<SoapPrimitive>)ret)
					machines.add((IMachine)Proxy.newProxyInstance(VBoxMonitorActivity.class.getClassLoader(), new Class [] { IMachine.class }, new SOAPInvocationHandler(p.toString(), "IMachine", URL)));
				return machines;
			}
			catch(Exception e)	{
				e.printStackTrace();
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final List<IMachine> result)	{
			ListView listView = (ListView)findViewById(R.id.machines_list);
			listView.setAdapter(new MachinesListAdapter(VBoxMonitorActivity.this, result));
			dismissProgress();
		}
	}
}