package com.meem.rssreader;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.sample.rssreader.R;

public class GetGraphicURL extends Activity {
	private GridView gv;
	private String source;
	private ArrayList<String> srcList = new ArrayList<String>();
	private LinearLayout ll;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_graphic_url);
		ll = (LinearLayout) findViewById(R.id.ll);
		setTitle("画像");
		Intent intent = getIntent();
		String url = intent.getStringExtra("URL");
		AsyncHtmlSource task = new AsyncHtmlSource(this);
		task.execute(url);
		try {
			source = task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// Pattern p =
		// Pattern.compile("<img.+?src=\\s*(?:[\\\"'])?([^ \\\"']*)[^>]*>",
		// Pattern.MULTILINE);
		Pattern p = Pattern.compile("http(s?)://[\\w\\.\\-/:&?,=#]+\\.(jpg|jpeg|gif|png|bmp)", Pattern.MULTILINE);
		Matcher m = p.matcher(source);
		while (m.find()) {
			srcList.add(m.group() + "\n");
		}

		Toast.makeText(this, "読み込み中...", Toast.LENGTH_LONG).show();

		gv = (GridView) findViewById(R.id.gv);
		gv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				//Toast.makeText(GetGraphicURL.this, position + "#Selected", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(GetGraphicURL.this, PreserveGraphicBrowser.class);
				intent.putExtra("URL", srcList.get(position));
				startActivity(intent);
			}
		});

		gv.setOnItemLongClickListener(new OnItemLongClickListener() {
	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
	        	//Toast.makeText(GetGraphicURL.this, position + "#LongSelected", Toast.LENGTH_SHORT).show();
	        	return true;
	        }
	    });
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int displayWidth = ll.getWidth();
		gv.setAdapter(new ImageGridViewAdapter(this, this, srcList, displayWidth));
		gv.invalidate();
	}

	// バックボタンを押したときの処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return super.onKeyDown(keyCode, event);
	}
}