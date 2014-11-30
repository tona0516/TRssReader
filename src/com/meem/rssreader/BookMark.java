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
		setTitle("�u�b�N�}�[�N");
		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);
		ListView listView = (ListView) findViewById(R.id.listview);
		readBookmark();

		// �A�_�v�^�[��ݒ肵�܂�
		listView.setAdapter(mAdapter);
		// ���X�g�r���[�̃A�C�e�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// �u���E�U�ɔ�΂�
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
				// �m�F�_�C�A���O�̐���
				AlertDialog.Builder alertDlg = new AlertDialog.Builder(BookMark.this);
				alertDlg.setTitle("�m�F");
				alertDlg.setMessage("�폜���܂����H");

				alertDlg.setPositiveButton("�͂�", new DialogInterface.OnClickListener() {
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
							Toast.makeText(getApplicationContext(), "�폜���܂���", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				alertDlg.setNegativeButton("������", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Cancel �{�^���N���b�N����
					}
				});
				// �\��
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

		// �A�C�e����ǉ����܂�
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
				currentItem.setTime("�o�^��:" + StringSplit[3]);
				mAdapter.add(currentItem);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}