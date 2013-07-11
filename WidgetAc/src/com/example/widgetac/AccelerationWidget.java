package com.example.widgetac;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AccelerationWidget extends AppWidgetProvider {

	private static final String TAG = "AccelerationWidget" ;
	Intent intent ;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//サービスを起動
		intent = new Intent(context, AccelerationButtonEventService.class) ;
		context.startService(intent) ;
		Log.v(TAG, "onUpdate") ;
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
