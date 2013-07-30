package com.example.widgetac;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

public class RadioCheck extends PhoneStateListener {
	int waveLevel ;
	String signalType ;
	Context context ;
	RemoteViews remoteview ;
	SignalStrength signalStrength ;

	public RadioCheck() {
	}

	public RadioCheck(Context _context) {
		context = _context ;
//		remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ; 
	}
	
	/**
	 * 電池の消費が激しいようならこれを破棄する
	 */
//	@Override
//	public void onDataConnectionStateChanged(int state) {
//		remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
//		switch (state) {
//		case TelephonyManager.DATA_CONNECTED:
//			remoteview.setTextViewText(R.id.stateType, "connected") ;
//			break;
//		case TelephonyManager.DATA_CONNECTING:
//			remoteview.setTextViewText(R.id.stateType, "connecting") ;
//			break;
//		case TelephonyManager.DATA_DISCONNECTED:
//			remoteview.setTextViewText(R.id.stateType, "disconnectied") ;
//			break;
//		}
//		Log.v("起動中", "") ;
//		ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
//		AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
//		manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
//		super.onDataConnectionStateChanged(state);
//	}
	
	@Override
	public void onSignalStrengthsChanged(SignalStrength _signalStrength) {
		signalStrength = _signalStrength ;
		new Thread(new Runnable() {
			Handler mHandler = new Handler() ;
			@Override
			public void run() {
				Method[] methods = android.telephony.SignalStrength.class.getMethods() ;
				for(Method mth : methods){
					int value = 0;
					//			Log.v("メソッド名", mth.getName()) ;
					if( mth.getName().equals("getLteDbm") ){
						try {
							value = (Integer)mth.invoke(signalStrength, new Object[]{}) ;
							waveLevel = value ; //ausLevelでとったらdBm = (value - 140)
							signalType = context.getString(R.string.signallte) ;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				//				Log.v("waveLevel", Integer.toString(waveLevel)) ;
				//LTEがDbmで取れないと戻り値がint型の最大値でかえってくるようだ。そもそもLTEの受け口がないと0が帰ってくるみたいだ
				if( waveLevel == Integer.MAX_VALUE || waveLevel == 0){ 
					if( signalStrength.isGsm() ){
						int value = signalStrength.getGsmSignalStrength() ;
						if( value != 99 ){
							waveLevel = value * 2 -113 ;
						}
						else {
							waveLevel = value ;
						}
						signalType = context.getString(R.string.signalgsm) ;
					}
					else {
						int strength = -1 ;
						if( signalStrength.getEvdoDbm() < 0 ){
							/*EVDO*/
							strength = signalStrength.getEvdoEcio() < signalStrength.getEvdoSnr() ? signalStrength.getEvdoEcio() : signalStrength.getEvdoSnr() ;
							signalType = context.getString(R.string.signalevdo) ;
						}
						else if( signalStrength.getCdmaDbm() < 0 ){
							/*CDMA*/
							strength = signalStrength.getCdmaDbm() < signalStrength.getCdmaEcio() ? signalStrength.getCdmaDbm() : signalStrength.getCdmaEcio() ;
							signalType = context.getString(R.string.signalcdma) ;						
						}
						waveLevel = strength ;
					}
				}
				Log.i("電波強度", Integer.toString(waveLevel)) ;
				mHandler.post(new Runnable() {

					@Override
					public void run() {
//						if(remoteview != null){
//							remoteview.setTextViewText(R.id.values, Integer.toString(waveLevel)) ;
//						}
						remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
						remoteview.setTextViewText(R.id.values, Integer.toString(waveLevel)) ;
						remoteview.setTextViewText(R.id.signaltype, signalType) ;
						//AppWidgetの画面を更新
						ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
						AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
						manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
					}
				}) ;
			}
		}).start() ;
		super.onSignalStrengthsChanged(signalStrength);
	}

}
