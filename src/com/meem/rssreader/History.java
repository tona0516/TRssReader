package com.meem.rssreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.rssreader.R;


public class History extends Activity {
	public static final int MENU_HISTORY_DELETE = Menu.FIRST;
	public static final String HISTORY_TMP_FILEPATH = Environment.getExternalStorageDirectory() + "/history.tmp";
	private RssListAdapter mAdapter;
	private ArrayList<Item> mItems;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);
		setTitle("履歴");
		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);
		ListView listView = (ListView) findViewById(R.id.listview);
		readHistory();

		// アダプターを設定します
		listView.setAdapter(mAdapter);
		// リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// ブラウザに飛ばす
				Item item = mItems.get(position);
				Intent intent = new Intent(History.this, Browser.class);
				intent.putExtra("URL", item.getUrl().toString());
				intent.putExtra("Title", item.getTitle().toString());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//メニューの追加
		menu.add(0, MENU_HISTORY_DELETE, 0, "履歴全削除");
		return super.onCreateOptionsMenu(menu);
	}

	// MENUの項目を押したときの処理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 更新
			case MENU_HISTORY_DELETE :
				// 履歴をすべて削除する
				File file = new File(RssReader.HISTORY_FILEPATH);
				try {
					file.delete();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mAdapter.clear();
				Toast.makeText(this, "全削除しました", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}
	public void readHistory(){
		File file = new File(RssReader.HISTORY_FILEPATH);
		file.getParentFile().mkdir();
		String line;
		BufferedReader br;
		Stack<String> stack = new Stack<String>();
		Item currentItem;
		String[] StringSplit;

		try {
			br = new BufferedReader(new FileReader(file));
			// スタックに積んで逆順にaddする
			while ((line = br.readLine()) != null) {
				line.replaceAll("\n", "");
				stack.push(line);
			}
			while (!stack.empty()) {
				StringSplit = stack.pop().split(",");
				currentItem = new Item();
				currentItem.setTitle(StringSplit[0]);
				currentItem.setUrl(StringSplit[1]);
				currentItem.setSitetitle(StringSplit[2]);
				currentItem.setTime("閲覧日:" + StringSplit[3]);
				mAdapter.add(currentItem);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}