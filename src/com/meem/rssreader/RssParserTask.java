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

	// �R���X�g���N�^
	public RssParserTask(RssReader activity, RssListAdapter adapter,
			String search) {
		mActivity = activity;
		mAdapter = adapter;
		mSearch = search;
	}

	// �^�X�N�����s��������ɃR�[�������
	@Override
	protected void onPreExecute() {
		// �v���O���X�o�[��\������
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setMessage("Now Loading...");
		mProgressDialog.show();
	}

	// �o�b�N�O���E���h�ɂ����鏈����S���B�^�X�N���s���ɓn���ꂽ�l�������Ƃ���
	@Override
	protected RssListAdapter doInBackground(String... params) {
		RssListAdapter result = null;
		InputStream is = null;
		try {
			// HTTP�o�R�ŃA�N�Z�X���AInputStream���擾����
			if(RssReader.nextReedFlag != 1) is = new URL(params[0]).openConnection().getInputStream();
			else is = RssReader.is;
			result = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// mAdapter.sort(new Comparator4Date());
		// �����ŕԂ����l�́AonPostExecute���\�b�h�̈����Ƃ��ēn�����
		return result;
	}

	// ���C���X���b�h��Ŏ��s�����
	@Override
	protected void onPostExecute(RssListAdapter result) {
		mProgressDialog.dismiss();
		mActivity.setListAdapter(result);
	}

	// XML���p�[�X����
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
								nextButton.setTitle("������ǂ�");
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