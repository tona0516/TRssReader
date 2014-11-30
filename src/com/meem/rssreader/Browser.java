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
		// URL�̎擾
		Intent intent = getIntent();
		String URL = intent.getStringExtra("URL");
		String Title = intent.getStringExtra("Title");
		setTitle(Title);

		// ���C�A�E�g�̍쐬
		LinearLayout oLayout = new LinearLayout(getApplicationContext());

		// �y�[�W�ǂݍ��ݒ��ɂ��邮���
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// �y�[�W�ǂݍ��݂̐i����_�ŕ\��
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// WebVIew�̐����E�ݒ�
		oWebview = Create_WebView();
		// WebView�����C�A�E�g�ɒǉ�
		oLayout.addView(oWebview);
		// ���C�A�E�g�̐ݒ�
		setContentView(oLayout);
		// �ŏ��ɓǂݍ��ރy�[�W�̐ݒ�
		oWebview.loadUrl(URL);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // �o�b�N�{�^���������ꂽ��A�O�̃y�[�W�ɖ߂�
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
	// �߂�E�i�ރ{�^���̒ǉ�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item1 = menu.add("�߂�");
		MenuItem item2 = menu.add("�i��");
		MenuItem item3 = menu.add("�u���E�U�ŊJ��");
		MenuItem item4 = menu.add("�X�V");
		MenuItem item5 = menu.add("�摜�\��(��)");
		OnMenuItemClickListener listener1 = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (oWebview.canGoBack()) {
					oWebview.goBack();
				} else {
					finish();
					oWebview.clearCache(true); // �f�t�H���g�ł͏I�����ɃL���b�V�����N���A����
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
				// �m�F�_�C�A���O�̐���
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(Browser.this);
				alertDlg.setTitle("�m�F");
				alertDlg.setMessage("���������ʂɏ���܂��B��낵���ł����H");

				alertDlg.setPositiveButton("�͂�", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// OK �{�^���N���b�N����
						// �\��
						Intent intent = new Intent();
						intent.setClass(Browser.this, GetGraphicURL.class);
						intent.putExtra("URL", oWebview.getUrl().toString());
						startActivity(intent);
					}
				});
				alertDlg.setNegativeButton("������", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Cancel �{�^���N���b�N����
					}
				});
				// �\��
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
		// JS�ɑΉ�
		if (!pref.getBoolean("javascript_service", false)) {
			view.getSettings().setJavaScriptEnabled(true);
		}
		// �g��ł���悤�ɂ���
		view.getSettings().setBuiltInZoomControls(true);
		// FLASH�Ή�
		// view.getSettings().setPluginsEnabled(true);
		// view.getSettings().setPluginState(PluginState.ON);
		// �摜��\��
		// view.getSettings().setBlockNetworkImage(true);
		// �L���b�V���ݒ�
		view.getSettings().setAppCacheEnabled(true);
		view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		setProgressBarIndeterminateVisibility(true);
		// setProgressBarVisibility(true);
		try {
			view.setWebViewClient(new WebViewClient() {
				// �y�[�W�̓ǂݍ��݊������ɌĂ΂��
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					// �v���Z�X�o�[�̕\���I��
					setProgressBarIndeterminateVisibility(false);
				}

				// �y�[�W�̓ǂݍ��ݎ��ɌĂ΂��
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					// �v���Z�X�o�[�̕\���J�n
					setProgressBarIndeterminateVisibility(true);
				}

				// �y�[�W�̓ǂݍ��ݑO�ɌĂ΂��Burl���Ƃ̏����͂����ŋL�q
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// ���[���[�̗����グ�X�L�[�������m
					if (url.substring(0, 7).equals("mailto:")) {
						view.stopLoading();
						Uri uri = Uri.parse(url);
						// ���[���[�𗧂��グ��
						Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
						startActivity(intent);
						finish();
						// Google Play�̃X�L�[�������m
					} else if (url.startsWith("market://")) {
						// Google Play�ɔ�΂�
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(i);
						return true;
					} else if (url.endsWith(".jpeg") || url.endsWith(".jpg") || url.endsWith(".png")) {
						Intent intent = new Intent();
						intent.setClass(Browser.this, PreserveGraphicBrowser.class);
						intent.putExtra("URL", url);
						startActivity(intent);
					} else {
						// ���͕��ʂɓǂݍ���
						view.loadUrl(url);
					}
					return true;
				}
			});
			/*
			 * // �C���W�P�[�^�̕\�� view.setWebChromeClient(new WebChromeClient() {
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