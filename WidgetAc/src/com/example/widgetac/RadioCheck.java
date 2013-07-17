package com.example.widgetac;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;
import android.widget.RemoteViews;

public class RadioCheck extends PhoneStateListener {
	int waveLevel ;
	Context context ;
	RemoteViews remoteview ;
	
	public RadioCheck() {
	}

	public RadioCheck(Context _context) {
		context = _context ;
		remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ; 
	}
	
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		Method[] methods = android.telephony.SignalStrength.class.getMethods() ;
		for(Method mth : methods){
			int value = 0;
			//			Log.v("メソッド名", mth.getName()) ;
			/**
			 * 後に使うかもしれないのでコメントアウトにとどめておく
			 */
			//			if( mth.getName().equals("getCdmaAsuLevel") || mth.getName().equals("getEvdoAsuLevel")
			//					|| mth.getName().equals("getGsmAsuLevel") || mth.getName().equals("getLteAsuLevel") ){
			//				try {
			//					value = (Integer) mth.invoke(signalStrength, new Object[]{}) ;
			//					Log.i("asu", Integer.toString(value)) ;
			//					Log.d("isDbm", Integer.toString(value * 2 - 113)) ;
			//				} catch (IllegalArgumentException e) {
			//					e.printStackTrace();
			//				} catch (IllegalAccessException e) {
			//					e.printStackTrace();
			//				} catch (InvocationTargetException e) {
			//					e.printStackTrace();
			//				}
			//				
			//			}
			//			if( mth.getName().equals("getCdmaDbm") || mth.getName().equals("getEvdoDbm")
			//					|| mth.getName().equals("getGsmDbm") || mth.getName().equals("getLteDbm") ){
			//				try {
			//					value = (Integer)mth.invoke(signalStrength, new Object[]{}) ;
			//					Log.d("getDbm", Integer.toString(value)) ;
			//				} catch (IllegalArgumentException e) {
			//					e.printStackTrace();
			//				} catch (IllegalAccessException e) {
			//					e.printStackTrace();
			//				} catch (InvocationTargetException e) {
			//					e.printStackTrace();
			//				}
			//			}
			if( mth.getName().equals("getLteDbm") ){
				try {
					value = (Integer)mth.invoke(signalStrength, new Object[]{}) ;
					waveLevel = value ;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		if( waveLevel >= 1000000 ){
			if( signalStrength.isGsm() ){
				int value = signalStrength.getGsmSignalStrength() ;
				if( value != 99 ){
					waveLevel = value * 2 -113 ;
				}
				else {
					waveLevel = value ;
				}
			}
			else {
				int strength = -1 ;
				if( signalStrength.getEvdoDbm() < 0 ){
					/*EVDO*/
					strength = signalStrength.getEvdoEcio() < signalStrength.getEvdoSnr() ? signalStrength.getEvdoEcio() : signalStrength.getEvdoSnr() ;
				}
				else if( signalStrength.getCdmaDbm() < 0 ){
					/*CDMA*/
					strength = signalStrength.getCdmaDbm() < signalStrength.getCdmaEcio() ? signalStrength.getCdmaDbm() : signalStrength.getCdmaEcio() ;
				}
				waveLevel = strength ;
			}
		}
		Log.i("電波強度", Integer.toString(waveLevel)) ;
		if(remoteview != null){
			remoteview.setTextViewText(R.id.values, Integer.toString(waveLevel)) ;
			//AppWidgetの画面を更新
			ComponentName thisWidget = new ComponentName(context, AccelerationWidget.class) ;
			AppWidgetManager manager = AppWidgetManager.getInstance(context) ;
			manager.updateAppWidget(thisWidget, remoteview) ; //此処をウィジェットのIDにしてやると特定のウィジェットに対して変更が出来る
		}
		super.onSignalStrengthsChanged(signalStrength);
	}
}
