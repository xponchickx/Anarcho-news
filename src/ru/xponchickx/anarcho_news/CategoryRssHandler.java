package ru.xponchickx.anarcho_news;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Html;

public class CategoryRssHandler extends DefaultHandler {
	private static final String LASTPAGE = "lastpage";
	private ArrayList<CategoryMessage> messages;
	private CategoryMessage currentMessage;
	private StringBuilder builder;
	private SaxFeedParser parser;
	public CategoryRssHandler(SaxFeedParser parser) {
		super();
		this.parser = parser;
	}
	public ArrayList<CategoryMessage> getMessages() {
		return messages;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (currentMessage != null) {
			if (localName.equalsIgnoreCase(SaxFeedParser.TITLE)) {
				currentMessage.setTitle(Html.fromHtml(builder.toString()).toString().trim());
			}
			if (localName.equalsIgnoreCase(SaxFeedParser.VIEW)) {
				currentMessage.setViews((builder.toString()).toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.ID)) {
				currentMessage.setId(builder.toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.IMAGE)) {
				StringBuffer imgSrc = new StringBuffer(builder.toString());
				imgSrc.insert(imgSrc.lastIndexOf("."), "_small");
				currentMessage.setImage(imgSrc.toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.PUB_DATE)) {
				currentMessage.setDate(builder.toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.ITEM)) {
				messages.add(currentMessage);
			}
		}
		if (localName.equalsIgnoreCase(SaxFeedParser.PAGE))
			if (builder.toString().trim().equals(LASTPAGE)) {
				parser.setLastPage(true);
			}
		builder.setLength(0);
	}
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		messages = new ArrayList<CategoryMessage>();
		builder = new StringBuilder();
	}
	@Override
	public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
		super.startElement(uri, localName, name, attrs);
		if (localName.equalsIgnoreCase(SaxFeedParser.ITEM)) {
			this.currentMessage = new CategoryMessage();
		}
	}
}