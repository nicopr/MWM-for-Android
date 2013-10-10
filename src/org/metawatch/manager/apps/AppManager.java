package org.metawatch.manager.apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.metawatch.manager.MetaWatch;
import org.metawatch.manager.MetaWatchService.Preferences;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppManager {

	static Map<String, ApplicationBase> apps = new HashMap<String, ApplicationBase>();
	
	public static void initApps(Context context) {
		sendDiscoveryBroadcast(context);
		if (getApp(MediaPlayerApp.APP_ID)==null)
			addApp(new MediaPlayerApp());
		if (getApp(ActionsApp.APP_ID)==null)
			addApp(new ActionsApp());
		if (getApp(CalendarApp.APP_ID)==null)
			addApp(new CalendarApp());
		if (getApp(AnalogFaceApp.APP_ID)==null)
			addApp(new AnalogFaceApp());
	}
	
	public static void addApp(ApplicationBase app) {
		apps.put(app.getId(), app);
	}
	
	public static void removeApp(ApplicationBase app) {
		if (apps.containsKey(app.getId())) {
			apps.remove(app.getId());
		}
	}
	
	public static ApplicationBase.AppData[] getAppInfos() {
		List<ApplicationBase.AppData> list = new ArrayList<ApplicationBase.AppData>();
		for (ApplicationBase a : apps.values()) {
			list.add(a.getInfo());
		}
		return list.toArray(new ApplicationBase.AppData[0]);
	}
	
	public static ApplicationBase getApp(String appId) {
		if(!apps.containsKey(appId)) {
			return null;
		}
		
		return apps.get(appId);
	}
	
	public static int getAppState(String appId) {
		if(!apps.containsKey(appId)) {
			return ApplicationBase.INACTIVE;
		}
		
		return apps.get(appId).appState;
	}
	
	public static void sendDiscoveryBroadcast(Context context) {
		if (Preferences.logging) Log.d(MetaWatch.TAG, "Broadcasting APPLICATION_DISCOVERY");
		Intent intent = new Intent("org.metawatch.manager.APPLICATION_DISCOVERY");		
		context.sendBroadcast(intent);
	}
}
