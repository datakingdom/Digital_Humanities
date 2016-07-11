package data.parser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import data.model.ConferenceURL;
import data.model.User;


public class UserProfileParse {
    private User u;

    public User getUser(String userID) {
        try {
            URL url = new URL(ConferenceURL.UserProfile + "userID=" + userID + "");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            UserProfileParseHandler handle = new UserProfileParseHandler();
            xmlreader.setContentHandler(handle);

            InputSource is = new InputSource(url.openStream());
            xmlreader.parse(is);

        } catch (Exception ee) {
            System.out.print(ee.toString());
        }

        return u;
    }

    private class UserProfileParseHandler extends DefaultHandler {
        private int state = 0;

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            if (qName.equals("Item")) {
                u = new User();
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
            if (qName.equals("email")) {
                state = 3;
                return;
            }
            if (qName.equals("userroleID")) {
                state = 4;
                return;
            }
            if (qName.equals("username")) {
                state = 5;
                return;
            }
            if (qName.equals("bio")) {
                state = 6;
                return;
            }
            if (qName.equals("personalWebSite")) {
                state = 7;
                return;
            }
            if (qName.equals("position")) {
                state = 8;
                return;
            }
            if (qName.equals("departmentAffiliation")) {
                state = 9;
                return;
            }
            if (qName.equals("organizationAffiliation")) {
                state = 10;
                return;
            }
            if (qName.equals("city")) {
                state = 11;
                return;
            }
            if (qName.equals("country")) {
                state = 12;
                return;
            }
            if (qName.equals("linkedin")) {
                state = 13;
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (qName.equals("Item")) {
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
                    u.setemail(content);
                    state = 0;
                    break;
                case 4:
                    u.setuserroleID(content);
                    state = 0;
                    break;
                case 5:
                    u.setusername(content);
                    state = 0;
                    break;
                case 6:
                    u.setbio(content);
                    state = 0;
                    break;
                case 7:
                    u.setpersonalwebsite(content);
                    state = 0;
                    break;
                case 8:
                    u.setposition(content);
                    state = 0;
                    break;
                case 9:
                    u.setdepartment(content);
                    state = 0;
                    break;
                case 10:
                    u.setorganization(content);
                    state = 0;
                    break;
                case 11:
                    u.setcity(content);
                    state = 0;
                    break;
                case 12:
                    u.setcountry(content);
                    state = 0;
                    break;
                case 13:
                    u.setlinkedin(content);
                    state = 0;
                    break;
                default:
                    state = 0;
                    return;
            }
        }
    }
}
