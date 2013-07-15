package com.example.widgetac;

import android.annotation.TargetApi;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AccelerationService extends Service implements SensorEventListener {

	RemoteViews remoteview ;
	SensorManager sencManager ;
	float[] gravity = new float[3] ;
	float[] lcurrentGravity = new float[3] ;
	float[] hcurrentGravity = new float[3] ;
	int count = 0 ;
	int lebel ;
	static int widgetID = 0;
	private BroadcastBattery batteryReceiver ;
	
	@Override
	public void onCreate() {
		super.onCreate();
		remoteview = new RemoteViews(getPackageName(), R.layout.accelerationwidget) ;
		initRemoteview() ;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		batteryReceiver = new BroadcastBattery() ;
		registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW)) ;
		widgetID = intent.getIntExtra("ID", 0) ;
		SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
		lebel = pref.getInt("Level", 5) ;
		sencManager = (SensorManager)getSystemService(SENSOR_SERVICE) ;
		sencManager.registerListener(this, sencManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI) ;
		//		sencManager.registerListener(this, sencManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI) ;
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(batteryReceiver) ;
		sencManager.unregisterListener(this) ;
		initRemoteview() ;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone() ;
			break;
		default:
			break;
		}

		//AppWidgetの画面を更新
		//ローパスフィルタ
		lcurrentGravity[0] = (float)( gravity[0] * 0.1 + lcurrentGravity[0] * 0.9 ) ;
		lcurrentGravity[1] = (float)( gravity[1] * 0.1 + lcurrentGravity[1] * 0.9 ) ;
		lcurrentGravity[2] = (float)( gravity[2] * 0.1 + lcurrentGravity[2] * 0.9 ) ;
		//ハイパスフィルタ
		hcurrentGravity[0] = (float)( gravity[0] - lcurrentGravity[0] ) ;
		hcurrentGravity[1] = (float)( gravity[1] - lcurrentGravity[1] ) ;
		hcurrentGravity[2] = (float)( gravity[2] - lcurrentGravity[2] ) ;

		for(int i = 0; i < hcurrentGravity.length; i++){
			if( hcurrentGravity[i] < 0 ){
				hcurrentGravity[i] = -hcurrentGravity[i];
			}
		}
		Log.v("X", Float.toString(hcurrentGravity[0])) ;
		Log.v("Y", Float.toString(hcurrentGravity[1])) ;
		Log.v("Z", Float.toString(hcurrentGravity[2])) ;
		if( hcurrentGravity[0] > 20 - lebel ){
			count++ ;
			remoteview.setTextViewText(R.id.x, Float.toString(hcurrentGravity[0])) ;
			remoteview.setTextViewText(R.id.y, Float.toString(hcurrentGravity[1])) ;
			remoteview.setTextViewText(R.id.z, Float.toString(hcurrentGravity[2])) ;
			remoteview.setTextViewText(R.id.count, Integer.toString(count)) ;
			//リモートビュー更新
			ComponentName thisWidget = new ComponentName(this, AccelerationWidget.class) ;
			AppWidgetManager manager = AppWidgetManager.getInstance(this) ;
			manager.updateAppWidget(thisWidget, remoteview) ;
			if(count > 4){
				Toast.makeText(this, "回復するぜ!", Toast.LENGTH_SHORT).show() ;
				airChange() ;
				count = 0 ;
			}
		}
	}

	public void initRemoteview(){
		count = 0 ;
		remoteview.setTextViewText(R.id.x, "0.0") ;
		remoteview.setTextViewText(R.id.y, "0.0") ;
		remoteview.setTextViewText(R.id.z, "0.0") ;
		remoteview.setTextViewText(R.id.count, Integer.toString(count)) ;
		//リモートビュー更新
		ComponentName thisWidget = new ComponentName(this, AccelerationWidget.class) ;
		AppWidgetManager manager = AppWidgetManager.getInstance(this) ;
		manager.updateAppWidget(thisWidget, remoteview) ;
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void airChange() {
		String state ;
		if( Build.VERSION.SDK_INT >= 17 ){
			state = Settings.System.getString(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON) ;
		}
		else {
			state = Settings.System.getString(getContentResolver(), Settings.System.AIRPLANE_MODE_ON) ;
		}
		//state 0:無効 1:有効
		boolean flag = state.equals("1") ? false : true ;  

		if( flag ){
			Intent airIntent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			if( Build.VERSION.SDK_INT >= 17 ){
				Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 1) ;
			}
			else {
				Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 1) ;
			}
			airIntent.putExtra("state", 1);
			sendBroadcast(airIntent);

			//valueの数値の反転
			if( Build.VERSION.SDK_INT >= 17 ){
				Settings.System.putInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) ;
			}
			else {
				Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) ;
			}
			airIntent.putExtra("state", 0);
			sendBroadcast(airIntent);
		}
		else {
			Toast.makeText(this, "機内モードだぜアンタ", Toast.LENGTH_SHORT).show() ;
		}
	}
	
	public synchronized void sleep(long msec){
		try {
			wait(msec) ;
		} 
		catch (Exception e) {
			e.printStackTrace() ;
		}
	}

}
