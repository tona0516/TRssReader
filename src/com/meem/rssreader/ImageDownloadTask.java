package com.meem.rssreader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloadTask {
	private ImageView iv;
	private String url;
	private Bitmap bitmap;
	private int displayWidth;
	private GetGraphicURL ggu;
	private ProgressDialog mProgressDialog;

	public ImageDownloadTask(GetGraphicURL ggu, ImageView iv, String url, int displayWidth) {
		this.ggu = ggu;
		this.iv = iv;
		this.url = url;
		this.displayWidth = displayWidth;
	}
	public void execute() {
		final Handler mHandler = new Handler();
		new Thread(new Runnable() {
			public void run() {
				bitmap = getBitmap(url);
				if (bitmap == null)
					return;
				// ポスト処理
				mHandler.post(new Runnable() {
					public void run() {
						// 画像のセット
						//bitmap = Bitmap.createScaledBitmap(bitmap, displayWidth / 2, displayWidth / 2, false);
						iv.setImageBitmap(bitmap);
						iv.setTag(url);
					}
				});
			}
		}).start();
	}

	/*
	 * 画像のダウンロード
	 */
	public Bitmap getBitmap(String urlRaw) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable = true;
		try {
			BufferedInputStream in = new BufferedInputStream((InputStream) (new URL(url)).getContent());
			bitmap = BitmapFactory.decodeStream(in, null, opts);
			bitmap = Bitmap.createScaledBitmap(bitmap, displayWidth / 2, displayWidth / 2, false);
			in.close();
		} catch (Exception ex) {
			return null;
		}
		return bitmap;
	}
}