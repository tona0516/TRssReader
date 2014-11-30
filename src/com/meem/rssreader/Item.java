package com.meem.rssreader;


public class Item {

	// 記事のタイトル
	// 記事の本文
	private CharSequence mTitle;
	private CharSequence mDescription;
	private CharSequence mUrl;
	private CharSequence mTime;
	private CharSequence mSitetitle;
	private boolean mAlready;

	public Item() {
		mTitle = "";
		mDescription = "";
		mSitetitle = "";
		mAlready = false;
		mTime = "";
		mUrl = "";
	}

	public CharSequence getDescription() {
		return mDescription;
	}

	public void setDescription(CharSequence description) {
		mDescription = description;
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public void setTitle(CharSequence title) {
		mTitle = title;
	}
	public CharSequence getUrl() {
		return mUrl;
	}

	public void setUrl(CharSequence url) {
		mUrl = url;
	}
	public CharSequence getTime() {
		return mTime;
	}

	public void setTime(CharSequence date) {
		mTime = date;
	}
	public CharSequence getSitetitle() {
		return mSitetitle;
	}
	public void setSitetitle(CharSequence sitetitle) {
		mSitetitle = sitetitle;
	}
	public boolean getAlready() {
		return mAlready;
	}
	public void setAlready(boolean already) {
		mAlready = already;
	}

}