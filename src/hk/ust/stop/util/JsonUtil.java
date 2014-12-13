package hk.ust.stop.util;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	/**
	 * @param userName
	 * @param password
	 * @return
	 * @throws JSONException
	 */
	public static String userInfo2Json(String userName, String password) {

		JSONObject sendJo = new JSONObject();
		try {
			sendJo.put("userName", userName);
			sendJo.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String content = sendJo.toString();
		return content;

	}

	/**
	 * @param responseData
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public static String jsonObjectTransfer(String responseData, String key) {

		JSONObject jo;
		String reply = null;
		try {
			jo = new JSONObject(responseData);
			reply = jo.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return reply;
	}
	
	
	/**
	 * This function would change the json object to a object that we have define
	 * @param responseData
	 * @param cls
	 * @return
	 */
	public static Object getObjectFromJson(String responseData, Class cls){
		try {
			return new ObjectMapper().readValue(responseData, cls);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
