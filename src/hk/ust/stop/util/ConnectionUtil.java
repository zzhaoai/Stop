package hk.ust.stop.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class ConnectionUtil {

	/**
	 * Connect Client with Server, send json to server and get response from server
	 * @param staticUrl : complete Connect Url
	 * @param sendJsonMsg : json sending to Server
	 * @return
	 */
	public static String post2Server(String staticUrl, String sendJsonMsg) {

		URL url = null;
		
		try {
			url = new URL(staticUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();// open HttpURLConnection
			// set http header
			urlConn.setDoInput(true); // set iostream
			urlConn.setDoOutput(true); // set iostream
			urlConn.setRequestMethod("POST"); // Post not use cache
			urlConn.setConnectTimeout(10000); // timeout
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Charset", "utf-8"); // character encoding
			urlConn.connect(); // build up connection while not sending request
			// write http content
			OutputStream outputStream = urlConn.getOutputStream();
			String content = sendJsonMsg;
			outputStream.write(content.getBytes());
			outputStream.flush(); // flush
			outputStream.close(); // close iostream

			if (urlConn.getResponseCode() == 200) {
				// read server feedback
				BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));// push Http request
				String retData = null;
				String responseData = "";
				while ((retData = in.readLine()) != null) {
					responseData += retData;
				}

				in.close();// close iostream
				urlConn.disconnect();// disconnect

				return responseData;

			} else {
				Log.i("json", "Network Error");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "NoMessage";

	}

	/**
	 * Connect Client with Server, get json from server
	 * @param staticUrl : complete url
	 * @return Json response
	 */
	public static String getFromServer(String staticUrl) {
		URL url = null;
		try {
			url = new URL(staticUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();// open connection with HttpURLConnection
			// read server feedback
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));// push Http request
			String retData = null;
			String responseData = "";
			while ((retData = in.readLine()) != null) {
				responseData += retData;
			}
			in.close();// close iostream
			urlConn.disconnect();// disconnect

			return responseData;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "NoMessage";
	}
	
}
