package com.meem.rssreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Browser extends Activity {

	private WebView oWebview;
	private SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		// URLの取得
		Intent intent = getIntent();
		String URL = intent.getStringExtra("URL");
		String Title = intent.getStringExtra("Title");
		setTitle(Title);

		// レイアウトの作成
		LinearLayout oLayout = new LinearLayout(getApplicationContext());

		// ページ読み込み中にぐるぐる回す
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// ページ読み込みの進捗を棒で表示
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// WebVIewの生成・設定
		oWebview = Create_WebView();
		// WebViewをレイアウトに追加
		oLayout.addView(oWebview);
		// レイアウトの設定
		setContentView(oLayout);
		// 最初に読み込むページの設定
		oWebview.loadUrl(URL);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // バックボタンが押されたら、前のページに戻る
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// oWebview.goBack();
			if (oWebview.canGoBack())
				oWebview.goBack();
			else
				finish();
			return true;
		}
		return false;
	}
	public void onClickReloadBtn(View v) {
		oWebview.reload();
	}
	// 戻る・進むボタンの追加
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item1 = menu.add("戻る");
		MenuItem item2 = menu.add("進む");
		MenuItem item3 = menu.add("ブラウザで開く");
		MenuItem item4 = menu.add("更新");
		MenuItem item5 = menu.add("画像表示(β)");
		OnMenuItemClickListener listener1 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (oWebview.canGoBack()) {
					oWebview.goBack();
				} else {
					finish();
					oWebview.clearCache(true); // デフォルトでは終了時にキャッシュをクリアする
				}
				return false;
			}
		};
		OnMenuItemClickListener listener2 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (oWebview.canGoForward()) {
					oWebview.goForward();
				}
				return false;
			}
		};
		OnMenuItemClickListener listener3 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Uri uri = Uri.parse(oWebview.getUrl());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				return false;
			}
		};
		OnMenuItemClickListener listener4 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				oWebview.reload();
				return false;
			}
		};
		OnMenuItemClickListener listener5 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				// 確認ダイアログの生成
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(Browser.this);
				alertDlg.setTitle("確認");
				alertDlg.setMessage("メモリを大量に消費します。よろしいですか？");

				alertDlg.setPositiveButton("はい", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// OK ボタンクリック処理
						// 表示
						Intent intent = new Intent();
						intent.setClass(Browser.this, GetGraphicURL.class);
						intent.putExtra("URL", oWebview.getUrl().toString());
						startActivity(intent);
					}
				});
				alertDlg.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Cancel ボタンクリック処理
					}
				});
				// 表示
				alertDlg.create().show();
				return true;
			}
		};
		item1.setOnMenuItemClickListener(listener1);
		item2.setOnMenuItemClickListener(listener2);
		item3.setOnMenuItemClickListener(listener3);
		item4.setOnMenuItemClickListener(listener4);
		item5.setOnMenuItemClickListener(listener5);
		return true;
	}

	private WebView Create_WebView() {
		WebView view = new WebView(this);
		// JSに対応
		if (!pref.getBoolean("javascript_service", false)) {
			view.getSettings().setJavaScriptEnabled(true);
		}
		// 拡大できるようにする
		view.getSettings().setBuiltInZoomControls(true);
		// FLASH対応
		// view.getSettings().setPluginsEnabled(true);
		// view.getSettings().setPluginState(PluginState.ON);
		// 画像非表示
		// view.getSettings().setBlockNetworkImage(true);
		// キャッシュ設定
		view.getSettings().setAppCacheEnabled(true);
		view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		setProgressBarIndeterminateVisibility(true);
		// setProgressBarVisibility(true);
		try {
			view.setWebViewClient(new WebViewClient() {
				// ページの読み込み完了時に呼ばれる
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					// プロセスバーの表示終了
					setProgressBarIndeterminateVisibility(false);
				}

				// ページの読み込み時に呼ばれる
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					// プロセスバーの表示開始
					setProgressBarIndeterminateVisibility(true);
				}

				// ページの読み込み前に呼ばれる。urlごとの処理はここで記述
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// メーラーの立ち上げスキームを検知
					if (url.substring(0, 7).equals("mailto:")) {
						view.stopLoading();
						Uri uri = Uri.parse(url);
						// メーラーを立ち上げる
						Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
						startActivity(intent);
						finish();
						// Google Playのスキームを検知
					} else if (url.startsWith("market://")) {
						// Google Playに飛ばす
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(i);
						return true;
					} else if (url.endsWith(".jpeg") || url.endsWith(".jpg") || url.endsWith(".png")) {
						Intent intent = new Intent();
						intent.setClass(Browser.this, PreserveGraphicBrowser.class);
						intent.putExtra("URL", url);
						startActivity(intent);
					} else {
						// 他は普通に読み込む
						view.loadUrl(url);
					}
					return true;
				}
			});
			/*
			 * // インジケータの表示 view.setWebChromeClient(new WebChromeClient() {
			 *
			 * @Override public void onProgressChanged(WebView view, int
			 * progress) { setProgress(progress * 100); if (progress == 100) {
			 * setProgressBarIndeterminateVisibility(false);
			 * setProgressBarVisibility(false); } } });
			 */
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return view;
	}
}