package com.meem.rssreader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.rssreader.R;

public class BookMark extends Activity {
	public static final String BOOKMARK_TMP_FILEPATH = Environment.getExternalStorageDirectory() + "/bookmark.tmp";
	private RssListAdapter mAdapter;
	private ArrayList<Item> mItems;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);
		setTitle("ブックマーク");
		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);
		ListView listView = (ListView) findViewById(R.id.listview);
		readBookmark();

		// アダプターを設定します
		listView.setAdapter(mAdapter);
		// リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// ブラウザに飛ばす
				Item item = mItems.get(position);
				Intent intent = new Intent(BookMark.this, Browser.class);
				intent.putExtra("URL", item.getUrl().toString());
				intent.putExtra("Title", item.getTitle().toString());
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			Item item;
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				item = mItems.get(position);
				// 確認ダイアログの生成
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(BookMark.this);
				alertDlg.setTitle("確認");
				alertDlg.setMessage("削除しますか？");

				alertDlg.setPositiveButton("はい", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(RssReader.BOOKMARK_FILEPATH);
						File tmp = new File(BOOKMARK_TMP_FILEPATH);
						file.getParentFile().mkdir();
						tmp.getParentFile().mkdir();
						BufferedReader br;
						BufferedWriter bw;
						String title = item.getTitle().toString();
						String url = item.getUrl().toString();
						String line;
						try {
							br = new BufferedReader(new FileReader(file));
							bw = new BufferedWriter(new FileWriter(tmp));
							while ((line = br.readLine()) != null) {
								line.replaceAll("\n", "");
								String[] StringSplit = line.split(",");
								if (title.equals(StringSplit[0]) && url.equals(StringSplit[1])) {
									mAdapter.remove(item);
								} else {
									bw.write(line + "\n");
								}
							}
							br.close();
							bw.flush();
							bw.close();
							file.delete();
							tmp.renameTo(file);
							Toast.makeText(getApplicationContext(), "削除しました", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				alertDlg.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Cancel ボタンクリック処理
					}
				});
				// 表示
				alertDlg.create().show();
				return true;
			}
		});
	}
	public void readBookmark() {
		String line;
		String[] StringSplit;
		File file = new File(RssReader.BOOKMARK_FILEPATH);
		file.getParentFile().mkdir();
		BufferedReader br;
		Stack<String> stack = new Stack<String>();
		Item currentItem;

		// アイテムを追加します
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
				currentItem.setTime("登録日:" + StringSplit[3]);
				mAdapter.add(currentItem);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}