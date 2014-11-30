package com.meem.rssreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

public class AsyncDownload extends AsyncTask<String, String, String> {

	private PreserveGraphicBrowser pgb;

	public AsyncDownload(PreserveGraphicBrowser main) {
		super();
		pgb = main;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... value) {
		String url = value[0];
		onDownload(url);
		return url;
	}

    @Override
    protected void onProgressUpdate(String... values) {
    }

    @Override
    protected void onPostExecute(String result) {
    }

	protected void onDownload(String url){
		try {
			File root = new File(Environment.getExternalStorageDirectory().getPath() + "/Toyo2ch/");
			if (!root.exists()) {
				root.mkdir();
			}

			Date mDate = new Date();
			SimpleDateFormat fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String fileName = fileNameDate.format(mDate) + ".jpg";
			String AttachName = root.getAbsolutePath() + "/" + fileName;

			// 出力ストリーム
			FileOutputStream out = new FileOutputStream(AttachName);

			// 入力ストリームで画像を読み込む
			URL mUrl = new URL(url);
			InputStream istream = mUrl.openStream();

			// 読み込んだファイルをビットマップに変換
			Bitmap oBmp = BitmapFactory.decodeStream(istream);
			// 保存
			oBmp.compress(CompressFormat.JPEG, 100, out);
			// 終了処理
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}