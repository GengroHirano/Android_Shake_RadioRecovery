package com.example.widgetac;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

public class AccelerationWidget extends AppWidgetProvider {

	private static final String TAG = "AccelerationWidget" ;
	private final String BUTTON_CLICK_ACTION = "ACCELERATION_BUTTON_CLICK_ACTION" ;
	Intent intent ;
	static TelephonyManager telManager = null;
	static RadioCheck check = null;
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		intent = new Intent(context, ButtonEventSettingService.class) ;
		intent.putExtra("WidgetID", appWidgetIds) ;
		context.startService(intent) ;
		
		Log.v(TAG, "onUpdate") ;
		//実はサービスでおんなじ処理をしている。二度手間感がとんでもないが、コレをしないと、少し古い端末でボタンが反応しなくなる不具合が発生する
//		for(int id : appWidgetIds){
			
			RemoteViews remoteview = new RemoteViews(context.getPackageName(), R.layout.accelerationwidget) ;
			if( isServiceRunning(AccelerationService.class.getCanonicalName(), context) ){
				remoteview.setTextViewText(R.id.widgetSW, context.getString(R.string.off)) ;
				remoteview.setInt(R.id.view, "setBackgroundColor", context.getResources().getColor(R.color.green)) ;
			}
			
			
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
			
//		}

		check = getRadioCheck(context) ;
		telManager = getTelephonyManager(context) ;
		telManager.listen(check, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS) ;
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	//起動しているwidgetが終了するとコレが呼ばれる
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v("タグ", "デリート") ;
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	//起動している全てのウィジェットが終了するとコレが呼ばれる
	public void onDisabled(Context context) {
		check = getRadioCheck(context) ;
		telManager = getTelephonyManager(context) ;
		telManager.listen(check, PhoneStateListener.LISTEN_NONE) ;
		check = null ;
		telManager = null ;
		if( isServiceRunning(AccelerationService.class.getCanonicalName(), context) ){
			intent = new Intent(context, AccelerationService.class) ;
			context.stopService(intent) ;
		}
		if( isServiceRunning(ButtonEventSettingService.class.getCanonicalName(), context) ){
			intent = new Intent(context, ButtonEventSettingService.class) ;
			context.stopService(intent) ;
//			Toast.makeText(context, "サービス終了", Toast.LENGTH_SHORT).show() ;
		}
		super.onDisabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
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

	
	//シングルトン
	public TelephonyManager getTelephonyManager(Context _context){
		if( telManager == null ){
			telManager = (TelephonyManager)_context.getSystemService(Context.TELEPHONY_SERVICE) ;
			Log.v("null", "作成") ;
		}
		return telManager ;
	}
	
	//シングルトン
	public RadioCheck getRadioCheck(Context _context){
		if( check == null ){
			check = new RadioCheck(_context) ;
			Log.v("null", "作成") ;
		}
		return check ;
	}
}
