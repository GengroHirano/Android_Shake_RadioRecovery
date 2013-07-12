package com.example.widgetac;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AccelerationButtonEventService extends Service {

	public static final String mServiceName = AccelerationService.class.getCanonicalName() ; 
	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	private boolean swService = false ;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//ボタンが押された時に発行するインテントを準備
		Intent buttonIntent = new Intent() ;
		buttonIntent.setAction(BUTTON_CLICK_ACTION) ;
		PendingIntent pendIntent = PendingIntent.getService(this, 0, buttonIntent, 0) ;
		RemoteViews remoteview = new RemoteViews(getPackageName(), R.layout.accelerationwidget) ;
		remoteview.setOnClickPendingIntent(R.id.wedgetSW, pendIntent) ;
		//ボタンが押された時に発行されたインテントの場合はSharedPreferences内のflagを逆転する
		if( BUTTON_CLICK_ACTION.equalsIgnoreCase(intent.getAction()) ){
			swService = !swService ;
			if( swService ){
				remoteview.setTextViewText(R.id.wedgetSW, getString(R.string.off)) ;

				Toast.makeText(this, "起動", Toast.LENGTH_SHORT).show() ;
				//サービスが既に起動しているかどうかを判断してから起動する
				intent = new Intent(this, AccelerationService.class) ;
				startService(intent) ;
			}
			else{
				remoteview.setTextViewText(R.id.wedgetSW, getString(R.string.on)) ;
				Toast.makeText(this, "終了", Toast.LENGTH_SHORT).show() ;
				//サービスを終了する
				intent = new Intent(this, AccelerationService.class) ;
				stopService(intent) ;
			}
		}

		//AppWidgetの画面を更新
		ComponentName thisWidget = new ComponentName(this, AccelerationWidget.class) ;
		AppWidgetManager manager = AppWidgetManager.getInstance(this) ;
		manager.updateAppWidget(thisWidget, remoteview) ;

		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if( isServiceRunning() ){
			Intent intent = new Intent(this, AccelerationService.class) ;
			stopService(intent) ;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private boolean isServiceRunning() {
	    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
	 
	    for (RunningServiceInfo info : services) {
	        if (mServiceName.equals(info.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}
