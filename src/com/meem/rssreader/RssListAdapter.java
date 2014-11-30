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
	// 1�s���Ƃ̃r���[�𐶐�����
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_row, null);
		}
		// ���ݎQ�Ƃ��Ă��郊�X�g�̈ʒu����Item���擾����
		Item item = this.getItem(position);

		if (item != null) {
			// Item����K�v�ȃf�[�^�����o���A���ꂼ��TextView�ɃZ�b�g����
			String title = item.getTitle().toString();
			mTitle = (TextView) view.findViewById(R.id.item_title);
			mTitle.setText(title);

			String time = item.getTime().toString();
			mTime = (TextView) view.findViewById(R.id.item_time);
			mTime.setText(time);

			String site = item.getSitetitle().toString();
			mSitetitle = (TextView) view.findViewById(R.id.item_sitetitle);
			mSitetitle.setText(site);

			// ���ǂ̃A�C�e���͊D�F�ɕ\������
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