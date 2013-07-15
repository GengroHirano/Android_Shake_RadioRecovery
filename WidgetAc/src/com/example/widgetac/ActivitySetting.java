package com.example.widgetac;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class ActivitySetting extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activitysetting) ;
		super.onCreate(savedInstanceState);
		SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
		//シークバーの設定
		SeekBar levelBar = (SeekBar)findViewById(R.id.seekBar1) ;
		levelBar.setProgress(pref.getInt("Level", 5)) ;
		//チェックボックスの設定
		CheckBox pushNotify = (CheckBox)findViewById(R.id.checkBox1) ;
		pushNotify.setChecked(pref.getBoolean("Check", true)) ;
		
		//リスナーの設定
		levelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.v("止めました", Integer.toString(seekBar.getProgress())) ;
				SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
				SharedPreferences.Editor edit = pref.edit() ;
				edit.putInt("Level", seekBar.getProgress()) ;
				edit.commit() ;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.v("start", "動かし始めてます") ;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Log.v("start", "動かしてます") ;
			}
		}) ;
		pushNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Log.i("チェック", Boolean.toString(isChecked)) ;
					SharedPreferences pref = getSharedPreferences("Setting", Context.MODE_PRIVATE) ;
					SharedPreferences.Editor edit = pref.edit() ;
					edit.putBoolean("Check", isChecked);
					edit.commit() ;
					
					//デバッグ用に残しておく
					if(pref.getBoolean("Check", false)){
						Toast.makeText(getApplicationContext(), "プッシュ通知有効", Toast.LENGTH_SHORT).show() ;
					}
					
					//電波強度を監視するサービス
			}
		}) ;
	}
}
