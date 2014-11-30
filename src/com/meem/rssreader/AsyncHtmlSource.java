package com.meem.rssreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncHtmlSource extends AsyncTask<String, String, String> {

	private GetGraphicURL ggu;
	private String source;

	public AsyncHtmlSource(GetGraphicURL main) {
		super();
		ggu = main;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected String doInBackground(String... value) {
		String urlString = value[0];
		URL url = null;

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			source = getSourceText(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return source;
	}

	@Override
	protected void onProgressUpdate(String... values) {
	}

	@Override
	protected void onPostExecute(String result) {
	}

	protected String getSourceText(URL url) throws IOException {
		InputStream in = url.openStream();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String s;
			while ((s = bf.readLine()) != null) {
				sb.append(s);
			}
		} finally {
			in.close();
		}
		return sb.toString();
	}
}