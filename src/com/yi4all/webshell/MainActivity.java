package com.yi4all.webshell;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	
	private WebView webView;
	
	private boolean isTwiceQuit;
	
	private String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return false;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, "");
			}
		}); 
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean flag = prefs.getBoolean("shortcut", false);
		if (!flag) {
		installShortCut(this, getString(R.string.app_name));
		}
		
		url = getUrl();
		
		if(url.length() == 0){
//			url = getString(R.string.org_url);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
		}
		
//		if(Build.VERSION.SDK_INT > 11){
//		ActionBar actionBar = getActionBar();
//		
//		if (actionBar != null) {
//			actionBar.hide();
//		}
//		}

	}
	
	public String getUrl() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		return prefs.getString("url", "");

	}

	@Override
	protected void onPause() {
		webView.clearHistory();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		webView.loadUrl(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_setting: {
			// popup the settings window
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		int keyCode = event.getKeyCode();
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
        	if (isTwiceQuit) {
        		webView.loadUrl(getString(R.string.org_url) + "user_loginOut.action");
        		this.finish();
			} else {
				toastMsg(R.string.sure_quit_app);
				isTwiceQuit = true;

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						isTwiceQuit = false;

					}
				}, 2000);
			}

        default:
            return false;
        }

	}
	
	public static void installShortCut(Context context, String appName) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
			// install shortcut
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");

			// 显示的名字
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					appName);
			// 显示的图标
			Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
					R.drawable.ic_launcher);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

			// 不允许重复创建
			shortcut.putExtra("duplicate", false);

			Intent intent = new Intent(context, MainActivity.class);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

			// 发送广播用以创建shortcut
			context.sendBroadcast(shortcut);

			// save flag
			Editor editor = prefs.edit();
			editor.putBoolean("shortcut", true);
			editor.commit();

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
