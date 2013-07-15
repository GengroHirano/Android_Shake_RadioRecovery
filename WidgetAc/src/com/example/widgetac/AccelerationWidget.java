package com.example.widgetac;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class AccelerationWidget extends AppWidgetProvider {

	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	private static final String TAG = "AccelerationWidget" ;
	Intent intent ;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.v(TAG, "onUpdate") ;
		//ウィジェットのボタンをクリックした時のためのペンディングインテントの準備
		for(int id : appWidgetIds){
			
			RemoteViews remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
			
			Intent settingIntent = new Intent(context, ActivitySetting.class) ;
			PendingIntent pIntent = PendingIntent.getActivity(context, id, settingIntent, 0) ;
			remoteview.setOnClickPendingIntent(R.id.goSetting, pIntent) ;
			
			Intent buttonIntent = new Intent() ;
			buttonIntent.setAction(BUTTON_CLICK_ACTION) ;
			PendingIntent pendIntent = PendingIntent.getService(context, id, buttonIntent, 0) ;
			remoteview.setOnClickPendingIntent(R.id.widgetSW, pendIntent) ;
			
			AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
			manager.updateAppWidget(id, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
			
		}
	}

	@Override
	//起動しているwidgetが終了するとコレが呼ばれる
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		intent = new Intent(context, AccelerationButtonEventService.class) ;
		context.stopService(intent) ;
		Log.v(TAG, "onDeleted") ;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
}
