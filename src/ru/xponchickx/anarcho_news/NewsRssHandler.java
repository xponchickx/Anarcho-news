package ru.xponchickx.anarcho_news;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Html;

public class NewsRssHandler extends DefaultHandler {

	private NewsMessage message;
	private StringBuilder builder;
	private boolean inElement = false;

	public NewsMessage getMessage() {
		return message;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (message != null && inElement) {
			builder.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (message != null) {
			if (localName.equalsIgnoreCase(SaxFeedParser.TITLE)) {
				message.setTitle(Html.fromHtml(builder.toString()).toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.FULL_TITLE)) {
				message.setFullText((builder.toString()).toString().trim());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.PUB_DATE)) {
				message.setDate(builder.toString());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.IMAGE)) {
				message.setImage(builder.toString());
			} else if (localName.equalsIgnoreCase(SaxFeedParser.LINK)) {
				message.setLink(builder.toString());
			}
			builder.setLength(0);
			inElement = false;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attrs) throws SAXException {
		super.startElement(uri, localName, name, attrs);
		if (localName.equalsIgnoreCase(SaxFeedParser.ITEM)) {
			this.message = new NewsMessage();
		}
		inElement = true;
	}
}
