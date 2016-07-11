package data.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.model.Conference;
import data.model.ConferenceURL;
import util.DateUtil;
import util.StrUtil;

public final class ConferenceInfoParser {

    public static void getConferenceInfo(String conferenceID) {

        InputStream stream = null;
        try {
            //Use Post Method
//            String urlString = "http://halley.exp.sis.pitt.edu/cn3/loadAllConferences.php?conferenceID=134";
            String urlString = ConferenceURL.ConferenceInfo + Conference.id;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            stream = conn.getInputStream();

            String jsonStr = StrUtil.convertToString(stream);

            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject conJson = (JSONObject) jsonArray.get(0);
            conJson.getString("shortTitle");

            Conference.id = conJson.getString("eventID");

            Conference.title = conJson.getString("title");
            Conference.description = conJson.getString("abstract");

            Conference.location = conJson.getString("location");
            Conference.twitter_item = conJson.getString("twitter_item");
            Conference.twitter_item_active = conJson.getString("twitter_item_active");
            Conference.twitter_widget_id = conJson.getString("twitter_widget_id");
            Conference.twitter_handler = conJson.getString("twitter_handler");
            Conference.twitter_hashtag = conJson.getString("twitter_hashtag");
            Conference.homepage = conJson.getString("home_page");

            // transform data
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(conJson.getString("beginDate"));
                Conference.startDate = DateUtil.Datetrans.get(formatter.format(date));

                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(conJson.getString("endDate"));
                Conference.endDate = DateUtil.Datetrans.get(formatter.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            stream.close();
        } catch (JSONException ee) {
            ee.printStackTrace();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
