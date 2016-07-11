package data.parser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import data.db.DBAdapter;
import data.model.Conference;


public class RecommendParse {
    private ArrayList<String> pidList = new ArrayList<String>();
    private String paperID;
    private DBAdapter db;

    public ArrayList<String> getRecom() {

        InputStreamReader isr = null;
        InputStream stream = null;

        try {
            //URL url = new URL(ConferenceURL.Recommend + "userID="+Conference.userID+"&conferenceID="+Conference.id+"");
            String urlString = "http://halley.exp.sis.pitt.edu/cn3mobile/contentBasedSysRec.jsp?userID=" + Conference.userID + "&conferenceID=" + Conference.id + "";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            stream = conn.getInputStream();

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xr = saxParser.getXMLReader();


            UserScheduleParseHandler handle = new UserScheduleParseHandler();
            xr.setContentHandler(handle);
            isr = new InputStreamReader(stream, "iso-8859-1");

            xr.parse(new InputSource(isr));
            stream.close();
            isr.close();


        } catch (Exception ee) {
            System.out.print(ee.toString());
        }

        return pidList;
    }


    private class UserScheduleParseHandler extends DefaultHandler {
        private int state = 0;

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            if (localName.equals("contentID")) {
                state = 1;
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals("contentID")) {
                pidList.add(paperID);
                return;
            }
        }

        public void characters(char ch[], int start, int length) {

            String content = new String(ch, start, length);
            switch (state) {
                case 1:
                    paperID = content;
                    state = 0;
                    break;
                default:
                    state = 0;
                    return;
            }
        }
    }
}
