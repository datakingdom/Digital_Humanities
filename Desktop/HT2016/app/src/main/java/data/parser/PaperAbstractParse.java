package data.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import util.StrUtil;


public class PaperAbstractParse {

    public String getPaperAbstract(String ID) {
        String data = null;
        try {
            URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/contentAbstract.jsp?contentID=" + ID);

            InputStream in = url.openStream();
            data = StrUtil.convertToString(in);

            in.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
