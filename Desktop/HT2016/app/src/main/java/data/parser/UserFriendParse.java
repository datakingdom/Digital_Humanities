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

import data.model.ConferenceURL;
import data.model.Friend;


public class UserFriendParse {
    public ArrayList<Friend> uList = new ArrayList<Friend>();

    public ArrayList<Friend> getIdList(String userID) {
        try {
            URL url = new URL(ConferenceURL.FriendList + "userID=" + userID + "");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            AttendingListParseHandler handle = new AttendingListParseHandler();
            xmlreader.setContentHandler(handle);

            InputSource is = new InputSource(url.openStream());
            xmlreader.parse(is);

        } catch (Exception ee) {
            System.out.print(ee.toString());
        }

        return uList;
    }

    private class AttendingListParseHandler extends DefaultHandler {
        private int state = 0;
        private Friend u;

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            if (qName.equals("Item")) {
                u = new Friend();
                return;
            }
            if (qName.equals("userID")) {
                state = 1;
                return;
            }
            if (qName.equals("name")) {
                state = 2;
                return;
            }
            if (qName.equals("requestStatus")) {
                state = 3;
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (qName.equals("Item")) {
                uList.add(u);
                return;
            }
        }

        public void characters(char ch[], int start, int length) {

            String content = new String(ch, start, length);
            switch (state) {
                case 1:
                    u.setid(content);
                    state = 0;
                    break;
                case 2:
                    u.setname(content);
                    state = 0;
                    break;
                case 3:
                    u.setstatus(content);
                    state = 0;
                    break;
                default:
                    state = 0;
                    return;
            }
        }
    }
}
