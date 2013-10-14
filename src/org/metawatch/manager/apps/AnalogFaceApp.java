package org.metawatch.manager.apps;

import java.util.Calendar;

import org.metawatch.communityedition.R;
import org.metawatch.manager.MetaWatch;
import org.metawatch.manager.MetaWatchService;
import org.metawatch.manager.Protocol;
import org.metawatch.manager.MetaWatchService.ConnectionState;
import org.metawatch.manager.MetaWatchService.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

public class AnalogFaceApp extends ApplicationBase {

	public final static String APP_ID = "org.metawatch.manager.apps.AnalogFaceApp";
	
	static AppData appData = new AppData() {{
		id = APP_ID;
		name = "Analog Face";
	
		supportsDigital = true;
		supportsAnalog = false;
	}};
	
  @Override
	public AppData getInfo() {
		return appData;
	}

  private static BroadcastReceiver mReceiver;
  
  @Override
	public void activate(Context context, int watchType) {

		if (Preferences.logging) Log.d(MetaWatch.TAG, "Activating AnalogFaceApp");	

		if (!MetaWatch.analogFaceAppStarted) {
			mReceiver = new BroadcastReceiver() {
			    @Override
			    public void onReceive(Context context, Intent intent) {   	
			    	/*
			    	 * first check if watched connected
			    	 * else, unregister Time Tick service and do nothing
			    	 */
					if (MetaWatchService.connectionState != ConnectionState.CONNECTED) {
						
						if (Preferences.logging) Log.d(MetaWatch.TAG, "Lost connexion to watch, unregistering TIME_TICK");
	
						context.unregisterReceiver(mReceiver);
						MetaWatch.analogFaceAppStarted=false; // app will get restarted and reregistered next time
	
					}
					else {
				    	if (Preferences.logging) Log.d(MetaWatch.TAG, "Received TIME_TICK");
				    	
						/*
				    	 *  following is probably not best solution: could not get the more logical call
				    	 *  	Application.updateAppMode(context);
				    	 *  nor
				    	 *		Bitmap bitmap = update(context, false, MetaWatchService.watchType);
				    	 *  	Application.updateAppMode(context, bitmap);
				    	 *  work properly. Help required
				    	 */	
				    	Bitmap bitmap = update(context, false, MetaWatchService.watchType);
				    	Protocol.sendLcdBitmap(bitmap, MetaWatchService.WatchBuffers.APPLICATION);
						Protocol.updateLcdDisplay(MetaWatchService.WatchBuffers.APPLICATION);
					}
			    }
			};
	
			if (Preferences.logging) Log.d(MetaWatch.TAG, "Registering TIME_TICK");
	
			context.registerReceiver (mReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
			MetaWatch.analogFaceAppStarted=true;				
		}
		else
			if (Preferences.logging) Log.d(MetaWatch.TAG, "Already registered, do nothing");
		
	}

	@Override
	public void deactivate(Context context, int watchType) {
		
		if (Preferences.logging) Log.d(MetaWatch.TAG, "Deactivating AnalogFaceApp, unregistering TIME_TICK");

		context.unregisterReceiver(mReceiver);
		MetaWatch.analogFaceAppStarted=false;
	}
	
	public Bitmap update(Context context, boolean preview, int watchType) {
		Bitmap bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);	
		
		canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.analogface2), 0, 0, null);
			
		Calendar cal=Calendar.getInstance();
		
		Bitmap minute = BitmapFactory.decodeResource(context.getResources(), R.drawable.minhand);
		Bitmap hour = BitmapFactory.decodeResource(context.getResources(), R.drawable.hourhand);
		
		Matrix matrixminute = new Matrix();
		matrixminute.reset();
		matrixminute.postRotate( cal.get(Calendar.MINUTE)*6, 48,48);
		canvas.drawBitmap(minute, matrixminute, null); 

		Matrix matrixhour = new Matrix();
		matrixhour.reset();
		matrixhour.postRotate( cal.get(Calendar.HOUR)*30 + cal.get(Calendar.MINUTE)/2, 48,48);
		canvas.drawBitmap(hour, matrixhour, null); 
		
		return bitmap;
	}
	
	public int buttonPressed(Context context, int id) {
		return BUTTON_NOT_USED;
	}

}
