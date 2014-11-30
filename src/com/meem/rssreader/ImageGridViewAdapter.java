package com.meem.rssreader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sample.rssreader.R;

public class ImageGridViewAdapter extends BaseAdapter {
	private List<String> urlList;
	private LayoutInflater inflater;
	private int displayWidth;
	private GetGraphicURL ggu;

	static class ViewHolder {
		ImageView iv_image;
	}

	public ImageGridViewAdapter(GetGraphicURL ggu, Context context, List<String> urlList, int displayWidth) {
		this.ggu = ggu;
		this.urlList = urlList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.displayWidth = displayWidth;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;

		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.imagegridview, null);
			// ‰æ‘œ
			holder.iv_image = (ImageView) view.findViewById(R.id.imagegridview_iv_image);
			// ‰æ‘œ‚Ì”ñ“¯ŠúDL
			new ImageDownloadTask(ggu, holder.iv_image, urlList.get(position), displayWidth).execute();
			// “o˜^
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		return view;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public int getCount() {
		return urlList.size();
	}
}