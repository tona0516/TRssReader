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
		setTitle("����");
		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);
		ListView listView = (ListView) findViewById(R.id.listview);
		readHistory();

		// �A�_�v�^�[��ݒ肵�܂�
		listView.setAdapter(mAdapter);
		// ���X�g�r���[�̃A�C�e�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// �u���E�U�ɔ�΂�
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
		//���j���[�̒ǉ�
		menu.add(0, MENU_HISTORY_DELETE, 0, "����S�폜");
		return super.onCreateOptionsMenu(menu);
	}

	// MENU�̍��ڂ��������Ƃ��̏���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// �X�V
			case MENU_HISTORY_DELETE :
				// ���������ׂč폜����
				File file = new File(RssReader.HISTORY_FILEPATH);
				try {
					file.delete();
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mAdapter.clear();
				Toast.makeText(this, "�S�폜���܂���", Toast.LENGTH_SHORT).show();
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
			// �X�^�b�N�ɐς�ŋt����add����
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
				currentItem.setTime("�{����:" + StringSplit[3]);
				mAdapter.add(currentItem);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}