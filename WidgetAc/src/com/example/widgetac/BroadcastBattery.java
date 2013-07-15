package com.example.widgetac;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class BroadcastBattery extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "バッテリー", Toast.LENGTH_SHORT).show() ;
		RemoteViews remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
		remoteview.setInt(R.id.view, "setBackgroundColor", context.getResources().getColor(R.color.blue)) ;
		ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
		AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
		manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
	}

}
