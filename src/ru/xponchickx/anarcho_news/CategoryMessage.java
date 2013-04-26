package ru.xponchickx.anarcho_news;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class CategoryMessage {
	private String title;
	private String id;
	private String image;
	private Date date;
	private String views;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String string) {
		this.image = string;
	}

	public void setDate(String date) {
		try {
			this.date = new SimpleDateFormat("yyyy-MM-dd hh:mm", new Locale("ru")).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getDate() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(this.date);
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getTime() {
		DateFormat format = new SimpleDateFormat("hh:mm");
		return format.format(this.date);
	}
}
