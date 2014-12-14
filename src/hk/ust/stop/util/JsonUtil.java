package hk.ust.stop.util;

import hk.ust.stop.model.GoodsInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.in;

import android.R.integer;

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
	
	public static List<GoodsInformation> tranfer2GoodsInfoList(String goodsInformationArray){
		
		List<GoodsInformation> goodsInformations = new ArrayList<GoodsInformation>();
		
		try{
			JSONObject jsonEntireObject = new JSONObject(goodsInformationArray);
			JSONArray jsonArray = jsonEntireObject.getJSONArray("objects");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject goodsItem = jsonArray.getJSONObject(i);
				GoodsInformation goodsInformation = new GoodsInformation();
				goodsInformation.setGoodsAddress(goodsItem.getString("address"));
				goodsInformation.setGoodsDescription(goodsItem.getString("goodsDescription"));
				goodsInformation.setGoodsName(goodsItem.getString("goodsName"));
				goodsInformation.setGoodsId(goodsItem.getInt("goodsid"));
				goodsInformation.setLatitude(goodsItem.getDouble("latitude"));
				goodsInformation.setLongitude(goodsItem.getDouble("longitude"));
				goodsInformation.setPictureName(goodsItem.getString("pictureName"));
				goodsInformation.setPrice(goodsItem.getDouble("price"));
				
				goodsInformations.add(goodsInformation);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return goodsInformations;
		
	}
	
}
