package com.example.widgetac;

import java.util.List;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.widget.RemoteViews;

public class ButtonEventSettingService extends Service {

//	public static final String mServiceName = AccelerationService.class.getCanonicalName() ;
	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	private Context context ;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//ウィジェットのペンディングインテントの登録
		context = this ;
//		new Thread(new Runnable() {
//			//実はAccelerationWidgetで同じ処理をしている。二度手間感がとんでもないが、コレを外すと、新しく配置したウィジェットのボタンが反応しなくなる
//			@Override
//			public void run() {
				RemoteViews remoteview = new RemoteViews(getPackageName(), R.layout.accelerationwidget) ;

				Intent settingIntent = new Intent(context, ActivitySetting.class) ;
				PendingIntent pIntent = PendingIntent.getActivity(context, 0, settingIntent, 0) ;
				remoteview.setOnClickPendingIntent(R.id.goSetting, pIntent) ;

				//ブロードキャスト発行設定を登録
				Intent buttonIntent = new Intent() ;
				buttonIntent.setAction(BUTTON_CLICK_ACTION) ;
				PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, 0) ;
				remoteview.setOnClickPendingIntent(R.id.widgetSW, pendIntent) ;

				ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
				AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
				manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
//			}
//		}).start() ;
		return START_REDELIVER_INTENT;
	}

	@Override
		public void onConfigurationChanged(Configuration newConfig) {

				RemoteViews remoteview = new RemoteViews(getPackageName(), R.layout.accelerationwidget) ;

				if( isServiceRunning(AccelerationService.class.getCanonicalName()) ){
					remoteview.setTextViewText(R.id.widgetSW, context.getString(R.string.off)) ;
					remoteview.setInt(R.id.view, "setBackgroundColor", context.getResources().getColor(R.color.green)) ;
				}
				
				Intent settingIntent = new Intent(this, ActivitySetting.class) ;
				PendingIntent pIntent = PendingIntent.getActivity(this, 0, settingIntent, 0) ;
				remoteview.setOnClickPendingIntent(R.id.goSetting, pIntent) ;

				Intent buttonIntent = new Intent() ;
				buttonIntent.setAction(BUTTON_CLICK_ACTION) ;
				PendingIntent pendIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0) ;
				remoteview.setOnClickPendingIntent(R.id.widgetSW, pendIntent) ;

				ComponentName thisWidget = new ComponentName(this, AccelerationWidget.class) ;
				AppWidgetManager manager = AppWidgetManager.getInstance(this) ;
				manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
				
				super.onConfigurationChanged(newConfig);
		}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private boolean isServiceRunning( String serviceName ) {
		ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo info : services) {
			if (serviceName.equals(info.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
}
