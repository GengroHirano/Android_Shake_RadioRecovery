package com.example.widgetac;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class AccelerationButtonEventBroadcast extends BroadcastReceiver {

//	public static final String mServiceName = AccelerationService.class.getCanonicalName() ;
	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = context.getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
		RemoteViews remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
		if( BUTTON_CLICK_ACTION.equalsIgnoreCase(intent.getAction()) ){
			if( !( isServiceRunning(AccelerationService.class.getCanonicalName(), context) ) ){
				remoteview.setTextViewText(R.id.widgetSW, context.getString(R.string.off)) ;
				remoteview.setInt(R.id.view, "setBackgroundColor", context.getResources().getColor(R.color.green)) ;
//				remoteview.setInt(R.id.view, "setBackgroundResource", R.drawable.ic_launcher) ;
				intent = new Intent(context, AccelerationService.class) ;
				context.startService(intent) ;
				if( pref.getBoolean("Check", false) ){
					//プッシュ通知サービス開始
				}
			}
			else{
				remoteview.setTextViewText(R.id.widgetSW, context.getString(R.string.on)) ;
				remoteview.setInt(R.id.view, "setBackgroundColor", context.getResources().getColor(R.color.black)) ;
				//サービスを終了する
				intent = new Intent(context, AccelerationService.class) ;
				context.stopService(intent) ;
				if( isServiceRunning("プッシュ通知する時に作成したサービス名", context) ){
					//ストップサービス
				}
			}
		}
		Log.v("", "イベントテスト") ;
		//AppWidgetの画面を更新
		ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
		AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
		manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る

	}
	

	private boolean isServiceRunning( String serviceName, Context context) {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo info : services) {
			if (serviceName.equals(info.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
