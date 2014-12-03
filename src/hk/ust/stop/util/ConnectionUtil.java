package hk.ust.stop.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
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
	
	
	@SuppressWarnings("deprecation")
	public static void uploadFile(Bitmap bm,String fileName)
    {
		try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                    "http://demo.engineerinme.com:5000/upload");
            
            ByteArrayBody bab = new ByteArrayBody(data,"image/png",fileName+".jpg");
            MultipartEntity reqEntity = new MultipartEntity(
            HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", bab);
            
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
 
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
        } catch (Exception e) {
            // handle exception here
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }
}
