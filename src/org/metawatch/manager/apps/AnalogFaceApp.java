package org.metawatch.manager.apps;

import java.util.Calendar;
import java.util.Locale;

import org.metawatch.communityedition.R;
import org.metawatch.manager.FontCache;
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
import android.text.TextPaint;
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
		
		if (Preferences.DayOfMonthOnAnalogFace) {
			int x;
			int y;
			int rectXleft;
			int rectXright;
			int rectYtop;
			int rectYbottom;

			if (Preferences.ShowHorizontalOnAnalogFace) {
				x=74;
				y=52;
				rectXleft=65;
				rectXright=82;
				rectYtop=43;
				rectYbottom=54;
			} else {
				x=48;
				y=77;
				if (Preferences.DayOfWeekOnAnalogFace) {
					rectXleft=35;
					rectXright=61;
				} else {
					rectXleft=39;
					rectXright=57;
				}
				rectYtop=68;
				rectYbottom=79;				
			}
			
			TextPaint paintMediumOutline = new TextPaint();
			paintMediumOutline.setColor(Color.BLACK);
			paintMediumOutline.setTextSize(FontCache.instance(context).Medium.size);
			paintMediumOutline.setTypeface(FontCache.instance(context).Medium.face);
			paintMediumOutline.setTextAlign(TextPaint.Align.CENTER);
			
			if (Preferences.FrameAroundDateAndMonthOnAnalogFace) {
				canvas.drawRect(rectXleft,rectYtop,rectXright,rectYbottom,paintMediumOutline);
				paintMediumOutline.setColor(Color.WHITE);
				canvas.drawRect(rectXleft+2,rectYtop+2,rectXright-2,rectYbottom-2,paintMediumOutline);
				paintMediumOutline.setColor(Color.BLACK);
			}
			
			canvas.drawText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), x, y, paintMediumOutline);

			if (Preferences.DayOfWeekOnAnalogFace) {
				if (Preferences.ShowHorizontalOnAnalogFace) {
					x=27;
					y=52;
					rectXleft=14;
					rectXright=40;
					rectYtop=43;
					rectYbottom=54;
				} else {
					x=48;
					y=68;
					rectXleft=35;
					rectXright=61;
					rectYtop=59;
					rectYbottom=70;				
				}
				
				if (Preferences.FrameAroundDateAndMonthOnAnalogFace) {
					canvas.drawRect(rectXleft,rectYtop,rectXright,rectYbottom,paintMediumOutline);
					paintMediumOutline.setColor(Color.WHITE);
					if (Preferences.DayOfMonthOnAnalogFace && !Preferences.ShowHorizontalOnAnalogFace)
						canvas.drawRect(rectXleft+2,rectYtop+2,rectXright-2,rectYbottom,paintMediumOutline);
					else
						canvas.drawRect(rectXleft+2,rectYtop+2,rectXright-2,rectYbottom-2,paintMediumOutline);
					paintMediumOutline.setColor(Color.BLACK);
				}
				
				String dayOfWeek=cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
				//crop to first 3 char. Using SHORT would not necessarily improve, FR locale for instance gives 4 char return, with '.' as last, for instance "MER."
				dayOfWeek=dayOfWeek.substring(0,(dayOfWeek.length()<3 ? dayOfWeek.length() : 3)); 
				
				canvas.drawText(dayOfWeek, x, y, paintMediumOutline);
			}
		}

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
