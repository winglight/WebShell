package com.yi4all.webshell;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);

		Button saveBtn = (Button) findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAccount();

			}
		});
		
		EditText urlTxt = (EditText) findViewById(R.id.urlTxt);
		urlTxt.setText(getUrl());

		final EditText nameTxt = (EditText) findViewById(R.id.shortcutNameTxt);
		nameTxt.setText(getPrefName());
		
		Button saveShortcutBtn = (Button) findViewById(R.id.saveShortcutBtn);
		saveShortcutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String oldName = getPrefName();
				
				Intent shortcutIntent = new Intent(getApplicationContext(),
			            MainActivity.class);
			    shortcutIntent.setAction(Intent.ACTION_MAIN);
			     
			    Intent addIntent = new Intent();
			    addIntent
			            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, oldName);
			 
			    addIntent
			            .setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
			    getApplicationContext().sendBroadcast(addIntent);
				
				savePrefName(nameTxt.getText().toString());

				MainActivity.installShortCut(SettingsActivity.this, nameTxt.getText().toString());
				
				finish();
				
			}
		});
	}

	private void saveAccount() {

		EditText urlTxt = (EditText) findViewById(R.id.urlTxt);

//		EditText passwordTxt = (EditText) findViewById(R.id.passwordTxt);
//		
//		if(passwordTxt.getText().toString().equals(generateHash(urlTxt.getText().toString()))){

		savePrefRelease(urlTxt.getText().toString());
		// successful message
		toastMsg(getString(R.string.saveSuccess));
		 this.finish();
//		}else{
//			toastMsg(getString(R.string.saveFailed));
//		}

	}
	
	private static String generateHash(String input){
		String hash = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(input.getBytes());
			BigInteger bigInt = new BigInteger(1, thedigest);
			hash = bigInt.toString(16);
			while (hash.length() < 32) {
				hash = "0" + hash;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hash.substring(24);
	}

	private void savePrefName(String url) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("name", url);
		editor.commit();
	}

	private String getPrefName() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getString("name", getString(R.string.app_name));

	}
	
	private void savePrefRelease(String url) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		editor.putString("url", url);
		editor.commit();
	}

	public String getUrl() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getString("url", "");

	}

	public void toastMsg(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public void toastMsg(int resId, String... args) {
		final String msg = this.getString(resId, args);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}