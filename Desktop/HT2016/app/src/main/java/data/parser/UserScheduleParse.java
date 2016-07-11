package data.parser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import data.db.DBAdapter;
import data.model.Conference;
import data.model.ConferenceURL;


public class UserScheduleParse {

    private ArrayList<String> pidList = new ArrayList<String>();
    private DBAdapter db;

    public ArrayList<String> getData() {
        try {
            //user id : 100004118
            URL url = new URL(ConferenceURL.Userschedule + "userid=" + Conference.userID + "&eventid=" + Conference.id + "");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            UserScheduleParseHandler handle = new UserScheduleParseHandler();
            xmlreader.setContentHandler(handle);

            InputSource is = new InputSource(url.openStream());
            xmlreader.parse(is);

        } catch (Exception ee) {
            System.out.print(ee.toString());
        }
        return pidList;
    }

    public ArrayList<String> getData(String userid) {
        try {
            URL url = new URL(ConferenceURL.Userschedule + "userid=" + userid + "&eventid=" + Conference.id + "");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            UserScheduleParseHandler handle = new UserScheduleParseHandler();
            xmlreader.setContentHandler(handle);

            InputSource is = new InputSource(url.openStream());
            xmlreader.parse(is);

        } catch (Exception ee) {
            System.out.print(ee.toString());
        }

        return pidList;
    }

    private class UserScheduleParseHandler extends DefaultHandler {
        private String paperID;
        private StringBuilder sb = new StringBuilder();

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            sb.setLength(0);
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals("paperID")) {
                paperID = sb.toString();
                pidList.add(paperID);
                return;
            }
        }

        public void characters(char ch[], int start, int length) {
            sb.append(ch, start, length);
        }
    }

}
