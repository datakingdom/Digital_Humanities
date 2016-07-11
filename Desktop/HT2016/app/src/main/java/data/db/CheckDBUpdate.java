package data.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import data.model.Conference;
import data.model.ConferenceURL;
import util.StrUtil;

public class CheckDBUpdate {
    public boolean needUpdate;


    public boolean compare() {
        String result = getTimestamp();
        if (result == null || !Character.isDigit(result.charAt(0)) ||
                result.compareTo(Conference.timstamp) == 0 || !Character.isDigit(Conference.timstamp.charAt(0)))
            needUpdate = false;
        else {
            needUpdate = true;
            Conference.timstamp = result;
        }

        return needUpdate;
    }

    public boolean check() {
        String result = getTimestamp();
        System.out.println("+++++++++++++++from server: " + result);
        System.out.println("+++++++++++++++: " + Conference.timstamp);
        needUpdate = !(result == null || !Character.isDigit(result.charAt(0)) ||
                result.compareTo(Conference.timstamp) == 0 || !Character.isDigit(Conference.timstamp.charAt(0)));

        return needUpdate;
    }

    public String getTimestamp() {
        String result = null;
        try {
            URL url = new URL(ConferenceURL.CheckUpdate + "eventID=" + Conference.id);
            InputStream in = url.openStream();
            result = StrUtil.convertToString(in);
            int start = result.indexOf("<timestamp>");
            int end = result.indexOf("</timestamp>");
            result = result.substring(start + 11, end);
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

