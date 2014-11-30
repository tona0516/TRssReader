package com.meem.rssreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.rssreader.R;

public class RssReader extends ListActivity {

	public static final int MENU_ITEM_RELOAD = Menu.FIRST;
	public static final int MENU_SEARCH = Menu.FIRST + 1;
	public static final int MENU_BOOKMARK = Menu.FIRST + 2;
	public static final int MENU_HISTORY = Menu.FIRST + 3;
	public static final int MENU_OPTION = Menu.FIRST + 4;
	public static final String BOOKMARK_FILEPATH = Environment.getExternalStorageDirectory() + "/bookmark.txt";
	public static final String HISTORY_FILEPATH = Environment.getExternalStorageDirectory() + "/history.txt";
	// private static final String RSS_FEED_URL =
	// "http://tona0516.php.xdomain.jp/index.php";
	// private static final String RSS_FEED_URL =
	// "http://192.168.0.7:81/rssFeed.xml";
	private static final String RSS_FEED_URL = "http://tonarssfeed.miraiserver.com/rssFeed.xml";
	private RssListAdapter mAdapter;
	private ArrayList<Item> mItems;
	private RssParserTask task;
	private SharedPreferences pref;
	public static int nextReedFlag = 0;
	public static InputStream is = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTitle("とよさんが作った2chまとめ");
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);
		task = new RssParserTask(this, mAdapter, "null");
		task.execute(RSS_FEED_URL);

		// 項目を長押しした時の挙動
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Item item = mItems.get(position);
				writeFile(BOOKMARK_FILEPATH, item);
				Toast.makeText(getApplicationContext(), "ブックマークに追加しました", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}

	// リストの項目を選択した時の処理
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = mItems.get(position);
		Intent intent = new Intent();
		item.setAlready(true);
		mAdapter.notifyDataSetChanged();

		if (item.getTitle().equals("続きを読む")) {
			//mAdapter = new RssListAdapter(this, mItems);
			task = new RssParserTask(this, mAdapter, "null");
			task.execute(RSS_FEED_URL);
			mAdapter.remove(item);
		} else {
			if (!pref.getBoolean("history_preserve", true)) {
				writeFile(HISTORY_FILEPATH, item);
			}

			if (pref.getBoolean("browser_select", false)) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl().toString())));
			} else {
				intent.setClass(this, Browser.class);
				intent.putExtra("URL", item.getUrl().toString());
				intent.putExtra("Title", item.getTitle().toString());
				startActivity(intent);
			}
		}
	}

	// MENUボタンを押したときの処理
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// メニューの追加
		menu.add(0, MENU_ITEM_RELOAD, 0, "更新");
		menu.add(0, MENU_SEARCH, 0, "検索");
		menu.add(0, MENU_BOOKMARK, 0, "ブックマーク");
		menu.add(0, MENU_HISTORY, 0, "履歴");
		menu.add(0, MENU_OPTION, 0, "設定");
		return super.onCreateOptionsMenu(menu);
	}

	// バックボタンを押したときの処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 確認ダイアログの生成
			AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
			alertDlg.setTitle("確認");
			alertDlg.setMessage("終了しますか？");

			alertDlg.setPositiveButton("はい", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// OK ボタンクリック処理
					finish();
				}
			});
			alertDlg.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Cancel ボタンクリック処理
				}
			});
			// 表示
			alertDlg.create().show();
		}
		return super.onKeyDown(keyCode, event);
	}

	// MENUの項目を押したときの処理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 更新
			case MENU_ITEM_RELOAD :
				nextReedFlag = 0;
				mItems = new ArrayList<Item>();
				mAdapter = new RssListAdapter(this, mItems);
				task = new RssParserTask(this, mAdapter, "null");
				task.execute(RSS_FEED_URL);
				break;
			case MENU_SEARCH :
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View layout = inflater.inflate(R.layout.custom_dialog, (ViewGroup) findViewById(R.id.layout_root));
				// アラーとダイアログ を生成
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("検索");
				builder.setView(layout);
				builder.setPositiveButton("検索", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// OK ボタンクリック処理
						EditText input = (EditText) layout.findViewById(R.id.customDlg_input);
						String search_str = input.getText().toString();
						mItems = new ArrayList<Item>();
						mAdapter = new RssListAdapter(RssReader.this, mItems);
						task = new RssParserTask(RssReader.this, mAdapter, search_str);
						task.execute(RSS_FEED_URL);
					}
				});
				builder.setNegativeButton("キャンセル", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Cancel ボタンクリック処理
					}
				});
				// 表示
				builder.show();
				break;
			case MENU_BOOKMARK :
				startActivity(new Intent(this, BookMark.class));
				break;
			case MENU_HISTORY :
				startActivity(new Intent(this, History.class));
				break;
			case MENU_OPTION :
				startActivityForResult(new Intent(this, Pref.class), 0);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/* ここで状態を保存 */
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		/* ここで保存した状態を読み出して設定 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// startActivityForResult で起動させたアクティビティが
		// finish() により破棄されたときにコールされる
	}

	public void writeFile(String FilePath, Item item) {
		File file = new File(FilePath);
		BufferedWriter bw;
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH'時'mm'分'");
		String str = item.getTitle() + "," + item.getUrl() + "," + item.getSitetitle() + "," + sdf.format(today) + "\n";

		file.getParentFile().mkdir();

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
			bw.write(str);
			bw.flush();
			bw.close();
		} catch (Exception e) {
		}
	}
}