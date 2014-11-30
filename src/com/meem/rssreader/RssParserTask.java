package com.meem.rssreader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Xml;

public class RssParserTask extends AsyncTask<String, Integer, RssListAdapter> {
	private RssReader mActivity;
	private RssListAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	private String mSearch;
	private XmlPullParser parser;
	private int eventType;
	private int itemSize = 0;

	// コンストラクタ
	public RssParserTask(RssReader activity, RssListAdapter adapter,
			String search) {
		mActivity = activity;
		mAdapter = adapter;
		mSearch = search;
	}

	// タスクを実行した直後にコールされる
	@Override
	protected void onPreExecute() {
		// プログレスバーを表示する
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setMessage("Now Loading...");
		mProgressDialog.show();
	}

	// バックグラウンドにおける処理を担う。タスク実行時に渡された値を引数とする
	@Override
	protected RssListAdapter doInBackground(String... params) {
		RssListAdapter result = null;
		InputStream is = null;
		try {
			// HTTP経由でアクセスし、InputStreamを取得する
			if(RssReader.nextReedFlag != 1) is = new URL(params[0]).openConnection().getInputStream();
			else is = RssReader.is;
			result = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// mAdapter.sort(new Comparator4Date());
		// ここで返した値は、onPostExecuteメソッドの引数として渡される
		return result;
	}

	// メインスレッド上で実行される
	@Override
	protected void onPostExecute(RssListAdapter result) {
		mProgressDialog.dismiss();
		mActivity.setListAdapter(result);
	}

	// XMLをパースする
	public RssListAdapter parseXml(InputStream is) throws IOException, XmlPullParserException {
		parser = Xml.newPullParser();
		parser.setInput(is, null);
		try {
			eventType = parser.getEventType();
			Item currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
					case XmlPullParser.START_TAG :
						tag = parser.getName();
						if (tag.equals("item")) {
							currentItem = new Item();
							itemSize++;
						} else if (currentItem != null) {
							if (tag.equals("title")) {
								currentItem.setTitle(parser.nextText());
							} else if (tag.equals("link")) {
								currentItem.setUrl(parser.nextText());
							} else if (tag.equals("author")) {
								currentItem.setSitetitle(parser.nextText());
							} else if (tag.equals("date") || tag.equals("pubDate")) {
								currentItem.setTime(parser.nextText());
							}
						}
						break;
					case XmlPullParser.END_TAG :
						tag = parser.getName();
						if (tag.equals("item")) {
							Pattern p = Pattern.compile(mSearch);
							Matcher m = p.matcher(currentItem.getTitle().toString());
							if (mSearch.equals("null") || m.find()) {
								mAdapter.add(currentItem);
							}
							/*
							if (itemSize == 30) {
								Item nextButton = new Item();
								nextButton.setTitle("続きを読む");
								mAdapter.add(nextButton);
								RssReader.nextReedFlag = 1;
								RssReader.is = is;
								itemSize = 0;
								return mAdapter;
							}
							*/
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mAdapter;
	}
}