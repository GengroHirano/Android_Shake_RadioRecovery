package com.example.widgetac;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

public class AccelerationButtonEventService extends Service {

//	public static final String mServiceName = AccelerationService.class.getCanonicalName() ;
	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	private boolean swService = false ;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
		RemoteViews remoteview = new RemoteViews(getPackageName(), R.layout.accelerationwidget) ;
		//ボタンが押された時に発行されたインテントの場合はSharedPreferences内のflagを逆転する
		if( BUTTON_CLICK_ACTION.equalsIgnoreCase(intent.getAction()) ){
			swService = !swService ;
			if( swService ){
				remoteview.setTextViewText(R.id.widgetSW, getString(R.string.off)) ;
				remoteview.setInt(R.id.view, "setBackgroundColor", getResources().getColor(R.color.green)) ;
//				remoteview.setInt(R.id.view, "setBackgroundResource", R.drawable.ic_launcher) ;
//				Toast.makeText(this, "起動", Toast.LENGTH_SHORT).show() ;
				intent = new Intent(this, AccelerationService.class) ;
				startService(intent) ;
				if( pref.getBoolean("Check", false) ){
					//プッシュ通知サービス開始
				}
			}
			else{
				remoteview.setTextViewText(R.id.widgetSW, getString(R.string.on)) ;
				remoteview.setInt(R.id.view, "setBackgroundColor", getResources().getColor(R.color.black)) ;
//				Toast.makeText(this, "終了", Toast.LENGTH_SHORT).show() ;
				//サービスを終了する
				intent = new Intent(this, AccelerationService.class) ;
				stopService(intent) ;
				if( isServiceRunning("プッシュ通知する時に作成したサービス名") ){
					//ストップサービス
				}
			}
		}

		//AppWidgetの画面を更新
		ComponentName thisWidget = new ComponentName(this, AccelerationWidget.class) ;
		AppWidgetManager manager = AppWidgetManager.getInstance(this) ;
		manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if( isServiceRunning(AccelerationService.class.getCanonicalName()) ){
			Intent intent = new Intent(this, AccelerationService.class) ;
			stopService(intent) ;
		}
		//もひとつ追加するかも
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private boolean isServiceRunning( String serviceName ) {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo info : services) {
			if (serviceName.equals(info.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
