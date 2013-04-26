package ru.xponchickx.anarcho_news;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class SaxFeedParser {

	static final String ID = "id";
	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String FULL_TITLE = "fulltext";
	static final String IMAGE = "image";
	static final String ITEM = "item";
	static final String LINK = "link";
	static final String PAGE = "page";
	static final String PUB_DATE = "pubDate";
	static final String VIEW = "view";
	static final String FILE = "file";
	static final String FILE_SIZE = "filesize";
	private URL mURL;
	private boolean isLastPage;
	public SaxFeedParser(String feedUrl) throws MalformedURLException {
		this.mURL = new URL(feedUrl);
	}
	private BufferedInputStream getInputStream() throws IOException {
		BufferedInputStream is;
		is = new BufferedInputStream(mURL.openConnection().getInputStream(), 8192);
		return is;
	}
	public ArrayList<CategoryMessage> categoriesParse() throws SAXException, IOException,
			ParserConfigurationException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			CategoryRssHandler handler = new CategoryRssHandler(this);
			parser.parse(this.getInputStream(), handler);
			this.getInputStream().close();
			return handler.getMessages();
		} finally {
			this.getInputStream().close();
		}
	}
	public NewsMessage newsParse() throws ParserConfigurationException, SAXException, IOException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			NewsRssHandler handler = new NewsRssHandler();
			parser.parse(this.getInputStream(), handler);
			return handler.getMessage();
		} finally {
			this.getInputStream().close();
		}
	}
	
	public BookMessage bookParse() throws ParserConfigurationException, SAXException, IOException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			BookRssHandler handler = new BookRssHandler();
			parser.parse(this.getInputStream(), handler);
			return handler.getMessage();
		} finally {
			this.getInputStream().close();
		}
	}
	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}
	public boolean isLastPage() {
		return isLastPage;
	}
}
