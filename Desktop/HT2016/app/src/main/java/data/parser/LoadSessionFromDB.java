package data.parser;


import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import data.model.Conference;
import data.model.Session;
import util.DateUtil;


public class LoadSessionFromDB {

    private ArrayList<Session> sList = new ArrayList<Session>();
    private Hashtable<String, String> Datetrans, Dtrans;


    public LoadSessionFromDB() {
        Datetrans = DateUtil.Datetrans;
        Dtrans = DateUtil.Dtrans;
    }

    public ArrayList<Session> getSessionData() {

        InputStreamReader isr = null;
        InputStream stream = null;
        try {
            //Use Post Method
            String urlString = new String("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?conferenceID=" + Conference.id);

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

            SessionParseHandler shandler = new SessionParseHandler();
            xr.setContentHandler(shandler);
            isr = new InputStreamReader(stream, "iso-8859-1");
            //InputStreamReader isr = new InputStreamReader(entity.getContent(),"UTF-8");

            xr.parse(new InputSource(isr));
            stream.close();
            isr.close();
        } catch (Exception ee) {
            System.out.print(ee.toString());
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (isr != null)
                    isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sList;
    }


    private class SessionParseHandler extends DefaultHandler {
        private Session se;
        private boolean sessionStart = false;
        private StringBuilder sb = new StringBuilder();

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            sb.setLength(0);

            if (localName.equals("SESSIONS")) {
                sessionStart = true;
                return;
            }
            if (localName.equals("SESSION")) {
                se = new Session();
                se.ID = atts.getValue("eventSessionID").toString();
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals("sessionName")) {
                se.name = sb.toString();
                return;
            }
            if (localName.equals("sessionDate")) {
                String content = sb.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String str = formatter.format(date);
                se.date = Datetrans.get(str);
                se.day_id = Dtrans.get(str);
                return;
            }
            if (localName.equals("beginTime") && sessionStart) {
                String content = sb.toString();
                SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
                Date date1 = new Date();
                try {
                    date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String str1 = formatter1.format(date1);
                se.beginTime = str1;
                return;
            }
            if (localName.equals("endTime") && sessionStart) {
                String content = sb.toString();
                SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
                Date date2 = new Date();
                try {
                    date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String str2 = formatter2.format(date2);
                se.endTime = str2;
                return;
            }
            if (localName.equals("location")) {
                se.room = sb.toString();
                return;
            }
            if (localName.equals("SESSION")) {
                sList.add(se);
                return;
            }
            if (localName.equals("SESSIONS")) {
                sessionStart = false;
                return;
            }
        }

        public void characters(char ch[], int start, int length) {
            sb.append(ch, start, length);
        }
    }
}

