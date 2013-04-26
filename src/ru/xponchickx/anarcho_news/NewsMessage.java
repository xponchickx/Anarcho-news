package ru.xponchickx.anarcho_news;

import java.net.MalformedURLException;
import java.net.URL;

public class NewsMessage {

	private String title;
	private String fullText;
	private String image;
	private URL link;
	private String date;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFullText(String text) {
		this.fullText = text;
	}

	public String getFullText() {
		return fullText;
	}

	public String getLink() {
		return link.toString();
	}

	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
