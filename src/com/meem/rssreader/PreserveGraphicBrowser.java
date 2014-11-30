package com.meem.rssreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PreserveGraphicBrowser extends Activity {
	WebView view;

	public static final int MENU_PRESERVE_GRAPHIC = Menu.FIRST;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String URL = intent.getStringExtra("URL");
		Toast.makeText(this, "メニューボタンから保存出来ます", Toast.LENGTH_SHORT).show();
		Log.v("url", URL);
		view = new WebView(this);
		view.getSettings().setBuiltInZoomControls(true);
		view.setWebViewClient(new WebViewClient());
		setContentView(view);
		view.loadUrl(URL);
	}

	// MENUボタンを押したときの処理
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューの追加
		menu.add(0, MENU_PRESERVE_GRAPHIC, 0, "保存");
		return super.onCreateOptionsMenu(menu);
	}

	// バックボタンを押したときの処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// MENUの項目を押したときの処理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 更新
			case MENU_PRESERVE_GRAPHIC :
				AsyncDownload task = new AsyncDownload(this);
				try {
					task.execute(view.getUrl());
					Toast.makeText(this, "succeeded to save the graphic", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(this, "failed to save the graphic", Toast.LENGTH_SHORT).show();
				}
		}
		return super.onOptionsItemSelected(item);
	}
}
