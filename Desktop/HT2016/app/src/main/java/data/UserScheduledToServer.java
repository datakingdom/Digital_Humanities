package data;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import data.model.Conference;
import data.model.ConferenceURL;

public class UserScheduledToServer {

    public String addScheduledPaper2Sever(String id) {
        String status = "";
        String url = ConferenceURL.AddToSchedule;
        HttpPost httpRequest = new HttpPost(url);

        // String testid = Conference.userID;

		/*
        try {
			SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
			if (getUserID.getString("userID", "").compareTo("") != 0) {
				Conference.userID = getUserID.getString("userID", "");
			}
		} catch (Exception e) {
		}
		*/

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Conference.userID));
        params.add(new BasicNameValuePair("contentid", id));
        try {

            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                int start = result.indexOf("<status>");
                int end = result.indexOf("</status>");
                result = result.substring(start, end);
                if (result
                        .compareTo("<status>OK") == 0)
                    status = "yes";
                if (result
                        .compareTo("<status>ERROR") == 0)
                    status = "no";

            } else {
                // System.out.print("error: status code not 200");
            }
        } catch (Exception e) {
            System.out.print("exception" + e);
        }

        return status;

    }

    public String DeleteScheduledPaper2Sever(String id) {
        String status = "";
        String url = ConferenceURL.DeleteFromSchedule;
        HttpPost httpRequest = new HttpPost(url);

        // String testid = Conference.userID;

		/*
        try {
			SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
			if (getUserID.getString("userID", "").compareTo("") != 0) {
				Conference.userID = getUserID.getString("userID", "");
			}
		} catch (Exception e) {
		}
		*/

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", Conference.userID));
        params.add(new BasicNameValuePair("contentID", id));
        try {

            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse httpResponse = new DefaultHttpClient()
                    .execute(httpRequest);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                int start = result.indexOf("<status>");
                int end = result.indexOf("</status>");
                result = result.substring(start, end);
                if (result
                        .compareTo("<status>OK") == 0)
                    status = "no";
                if (result
                        .compareTo("<status>ERROR") == 0)
                    status = "yes";

            } else {
                // System.out.print("error: status code not 200");
            }
        } catch (Exception e) {
            System.out.print("exception" + e);
        }

        return status;

    }
}
