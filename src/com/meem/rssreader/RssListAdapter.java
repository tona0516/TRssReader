package com.meem.rssreader;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sample.rssreader.R;


public class RssListAdapter extends ArrayAdapter<Item> {
	private LayoutInflater mInflater;
	private TextView mTitle;
	private TextView mTime;
	private TextView mSitetitle;

	public RssListAdapter(Context context, List<Item> objects) {
		super(context, 0, objects);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	// 1行ごとのビューを生成する
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_row, null);
		}
		// 現在参照しているリストの位置からItemを取得する
		Item item = this.getItem(position);

		if (item != null) {
			// Itemから必要なデータを取り出し、それぞれTextViewにセットする
			String title = item.getTitle().toString();
			mTitle = (TextView) view.findViewById(R.id.item_title);
			mTitle.setText(title);

			String time = item.getTime().toString();
			mTime = (TextView) view.findViewById(R.id.item_time);
			mTime.setText(time);

			String site = item.getSitetitle().toString();
			mSitetitle = (TextView) view.findViewById(R.id.item_sitetitle);
			mSitetitle.setText(site);

			// 既読のアイテムは灰色に表示する
			if (item.getAlready()) {
				mTitle.setTextColor(Color.GRAY);
				mTime.setTextColor(Color.GRAY);
				mSitetitle.setTextColor(Color.GRAY);
			} else {
				mTitle.setTextColor(Color.BLACK);
				mTime.setTextColor(Color.BLACK);
				mSitetitle.setTextColor(Color.BLACK);
			}
		}
		return view;
	}
}