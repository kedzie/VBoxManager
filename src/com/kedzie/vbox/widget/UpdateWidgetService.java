package com.kedzie.vbox.widget;

import java.io.IOException;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.server.ServerSQlite;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Update widgets
 * @author Marek Kędzierski
 */
public class UpdateWidgetService extends IntentService {
	private static final String TAG = "UpdateWidgetService";
	
	public UpdateWidgetService() {
		super("Update Widget Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		if(widgetIds==null)
		    return;
		for(int widgetId : widgetIds) {
			Log.i(TAG, "Updating App Widget: " + widgetId);
			String machineName = Provider.loadPref(this, widgetId, Provider.KEY_NAME);
			if(Utils.isEmpty(machineName))
			    continue;
			String machineId = Provider.loadPref(this, widgetId, Provider.KEY_IDREF);
			Server server = loadServer(Long.valueOf(Provider.loadPref(this, widgetId, Provider.KEY_SERVER)));
			VBoxSvc vboxApi = new VBoxSvc(server);
			IMachine machine = vboxApi.getProxy(IMachine.class, machineId);
			if(Utils.isEmpty(machine.getInterfaceName())) {      //check if logged on
			    machine = loginAndFindMachine(vboxApi, machineName);
			    if(machine==null)
			        return;
			    Provider.savePref(this, widgetId, Provider.KEY_IDREF, machine.getIdRef());
			}
			MachineView.cacheProperties(machine);
			Provider.updateAppWidget(this, widgetId, machine);
		}
	}
	
	private IMachine loginAndFindMachine(VBoxSvc vboxApi, String machineName) {
	    try {
            vboxApi.logon();
            for(IMachine m : vboxApi.getVBox().getMachines()) {
                if(m.getName().equals(machineName))
                    return m;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error logging on", e);
        }
	    return null;
	}
	
	private Server loadServer(long id) {
	    ServerSQlite db = new ServerSQlite(this);
        try {
         return db.get(id);   
        } finally {
            db.close();
        }
	}
}
