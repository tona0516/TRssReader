package com.meem.rssreader;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

public class Comparator4Date implements Comparator<Item> {
	public int compare(Item o1, Item o2) {
		Date n1 = null;
		Date n2 = null;
		try {
			n1 = DateFormat.getDateInstance().parse(o1.getTime().toString());
			n2 = DateFormat.getDateInstance().parse(o1.getTime().toString());
		} catch (ParseException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		try {
			// 日付の降順にソートする
			if (n1.after(n2)) {
				return -1;

			} else if (n1.before(n2)) {
				return 1;

			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

}