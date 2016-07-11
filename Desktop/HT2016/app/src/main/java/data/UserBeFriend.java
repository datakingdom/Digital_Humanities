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

public class UserBeFriend {
    public String getStatus(String id, String status) {
        String url = ConferenceURL.BeFriend;
        HttpPost httpRequest = new HttpPost(url);


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("requestUserID", Conference.userID));
        params.add(new BasicNameValuePair("respondUserID", id));
        params.add(new BasicNameValuePair("requestStatus", status));
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
                        .compareTo("<status>OK") == 0) {
                    if (status.compareTo("1") == 0)
                        status = "1";
                    else if (status.compareTo("4") == 0)
                        status = "4";
                } else
                    status = "0";
            } else {
                // System.out.print("error: status code not 200");
            }
        } catch (Exception e) {
            System.out.print("exception" + e);
        }

        return status;

    }
}