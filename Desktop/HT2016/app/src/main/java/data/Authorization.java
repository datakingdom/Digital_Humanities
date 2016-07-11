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

import data.model.ConferenceURL;

public class Authorization {
	public boolean isLoginOK    = false;
	public boolean isSignUp     = false;
	public String  userID       = null;
	public String  userName     = null;
	public String  errorMessage = null;

	public void login(String email, String password) {
		// delete whitespace string
		email = email.trim();
		password = password.trim();

		String url = ConferenceURL.Login;
		HttpPost httpRequest = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			//System.out.print(httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				int start = result.indexOf("<status>");
				int end = result.indexOf("</status>");
				String status;
				//end = result.indexOf("\r\n");
				status = result.substring(start+8, end);
				
				if(status.compareTo("OK")==0)
				{
					isLoginOK = true;
					int startUID = result.indexOf("<UserID>");
					int endUID = result.indexOf("</UserID>");
					userID = result.substring(startUID+17, endUID-3);
					int startUName = result.indexOf("<name>");
					int endUName = result.indexOf("</name>");
					userName = result.substring(startUName+15, endUName-3);
				}
				else
				{
					isLoginOK = false;
					errorMessage = result.substring(end+2,result.length()-17);
					System.out.print("error" + errorMessage);
				}
				
			} else {
				isLoginOK = false;
				System.out.print("error: status code not 200");
			}
		} catch (Exception e) {
			isLoginOK = false;
			System.out.print("exception" + e);
		}

	}
	
	public void signUp(String name,String email, String password, String rePassword, String citeULike)
	{
		// delete whitespace string
		name = name.trim();
		email = email.trim();
		password = password.trim();

		String url = ConferenceURL.Signup;
		HttpPost httpRequest = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("citeulike", citeULike));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				int start = result.indexOf("<status>");
				int end = result.indexOf("</status>");
				String status=null;
				//end = result.indexOf("\r\n");
				status = result.substring(start+8, end);
				
				if(status.compareTo("OK")==0)
				{
					isSignUp = true;
					int startUser = result.indexOf("<UserID>");
					int endUser = result.indexOf("</UserID>");
					userID = result.substring(startUser+17,endUser-3);
				}
				else
				{
					isSignUp = false;
					errorMessage = result.substring(end+2,result.length()-18);
					System.out.print("error");
				}
				
			} else {
				isSignUp = false;
				System.out.print("error");
			}
		} catch (Exception e) {
			isSignUp = false;
			System.out.print("Exception" + e);
		}
	}

	/*
	public void WriteSettings(String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;

		try {
			File file = new File("text.txt");
			if(!file.exists())
		        file.createNewFile();
			fOut = new FileOutputStream(file);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.flush();
			// Toast.makeText(context,
			// "Settings saved",Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(context,
			// "Settings not saved",Toast.LENGTH_SHORT).show();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/
}
