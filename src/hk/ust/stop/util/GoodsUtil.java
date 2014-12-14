package hk.ust.stop.util;

import hk.ust.stop.model.GoodsInformation;

/**
 * This class is used to upload to and download goods information from server
 * @author XJR
 *
 */
public class GoodsUtil {

	private static final String EMPTY_RESPONSE;
	private static final String NO_MESSAGE;
	
	static {
		EMPTY_RESPONSE = "{}";
		NO_MESSAGE = "NoMessage";
	}
	
	
	/**
	 * Use this function to upload information about a single product to 
	 * the server.
	 * @param goods
	 * @return
	 */
	public static boolean uploadGoodsInformation(GoodsInformation goods) {
		String response = ConnectionUtil.getFromServer(
				ServerUrlUtil.AddProductUrl(goods));
		
		if(response.equals(EMPTY_RESPONSE))
			return false;
		else {
			return true;
		}
	}
	
	
	/**
	 * Use this function to get information about a single product according 
	 * to its productId.
	 * @param productId
	 * @return
	 */
	public static GoodsInformation getProductById(long productId) {
		String responseData = ConnectionUtil.getFromServer(
				ServerUrlUtil.ProductByIdUrl(productId));
		
		if(responseData.equals(EMPTY_RESPONSE) ||
				responseData.equals(NO_MESSAGE))
			return null;
		else {
			GoodsInformation goods = new GoodsInformation();
			goods.setGoodsId(
					Long.parseLong(
					JsonUtil.jsonObjectTransfer(responseData, "goodsid")));
			goods.setGoodsName(
					JsonUtil.jsonObjectTransfer(responseData, "goodsName"));
			goods.setPictureName(
					JsonUtil.jsonObjectTransfer(responseData, "pictureName"));
			goods.setGoodsDescription(
					JsonUtil.jsonObjectTransfer(responseData, "goodsDescription"));
			goods.setGoodsAddress(
					JsonUtil.jsonObjectTransfer(responseData, "address"));
			goods.setLatitude(
					Double.parseDouble(
					JsonUtil.jsonObjectTransfer(responseData, "latitude")));
			goods.setLongitude(
					Double.parseDouble(
					JsonUtil.jsonObjectTransfer(responseData, "longitude")));
			goods.setPrice(
					Double.parseDouble(
					JsonUtil.jsonObjectTransfer(responseData, "price")));
			return goods;
		}
	}
	
	
	/**
	 * Use the filename as the url to get analyse result from the server.
	 * Notice: before calling this function, the picture should be uploaded
	 * 		   to the server.
	 * @param filename
	 * @return
	 */
	public static String analyzePicture(String filename) {
		String responseData = ConnectionUtil.getFromServer(
				ServerUrlUtil.analysePictureUrl(filename));
		
		if(responseData.equals(EMPTY_RESPONSE) ||
				responseData.equals(NO_MESSAGE))
			return null;
		
		return JsonUtil.jsonObjectTransfer(responseData, "output").trim();
	}
}
