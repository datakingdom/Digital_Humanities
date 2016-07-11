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
import data.model.UserBreif;


public class UserAttendingParse {

    public ArrayList<UserBreif> uList = new ArrayList<UserBreif>();

    public ArrayList<UserBreif> getIdList(String paperID) {
        try {
            URL url = new URL(ConferenceURL.AttendingList + "contentID=" + paperID);
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
        private UserBreif u;

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            if (qName.equals("Item")) {
                u = new UserBreif();
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
            if (qName.equals("username")) {
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
                    u.setusername(content);
                    state = 0;
                    break;
                default:
                    state = 0;
                    return;
            }
        }
    }
}
