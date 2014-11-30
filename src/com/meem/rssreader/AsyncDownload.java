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

			// �o�̓X�g���[��
			FileOutputStream out = new FileOutputStream(AttachName);

			// ���̓X�g���[���ŉ摜��ǂݍ���
			URL mUrl = new URL(url);
			InputStream istream = mUrl.openStream();

			// �ǂݍ��񂾃t�@�C�����r�b�g�}�b�v�ɕϊ�
			Bitmap oBmp = BitmapFactory.decodeStream(istream);
			// �ۑ�
			oBmp.compress(CompressFormat.JPEG, 100, out);
			// �I������
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}