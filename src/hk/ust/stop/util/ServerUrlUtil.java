package hk.ust.stop.util;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import hk.ust.stop.model.GoodsInformation;

/**
 * This class is used to provide the url for communicate with server,
 * it just simply combine base url with parameter
 * @author XJR
 *
 */

public class ServerUrlUtil {

	private static final String SERVER_BASE = "http://demo.engineerinme.com:5000/";
	
	/**
	 * Return url like this: http://demo.engineerinme.com:5000/login/username/password
	 * @param name
	 * @param password
	 * @return
	 */
	public static String LoginUrl(String name, String password) {
		return SERVER_BASE + "login/" + name + "/" + password + "/";
	}
	
	
	/**
	 * Return url like this: http://demo.engineerinme.com:5000/product/productId
	 * @param productId
	 * @return
	 */
	public static String ProductByIdUrl(long productId) {
		return SERVER_BASE + "product/" + productId + "/";
	}
	
	
	/**
	 * Return url like this: http://demo.engineerinme.com:5000/register/username/password/<email/phone
	 * @param name
	 * @param password
	 * @param email
	 * @param phone
	 * @return
	 */
	public static String AddUserUrl(String name, String password,
								String email, String phone) {
		return SERVER_BASE + "register/" + name + "/" + email
				+ "/" + phone + "/" + password + "/";
	}
	
	
	/**
	 * Url for adding a product to the server.
	 * @param goods
	 * @return
	 */
	public static String AddProductUrl(GoodsInformation goods) {
		return SERVER_BASE + "AddProduct/" +
				goods.getPrice() + "/" +
				goods.getPictureName() + "/" +
				goods.getLongitude() + "/" +
				goods.getLatitude() + "/" +
				goods.getGoodsName() + "/" +
				goods.getGoodsDescription() + "/" +
				goods.getGoodsAddress() + "/" +
				(null == AccountUtil.getLoginUser()?"0":AccountUtil.getLoginUser().getUserId())
				 + "/";
	}
	
	
	/**
	 * Url for searching products with the productName.
	 * @param productName
	 * @return
	 */
	public static String SearchProductUrl(String productName) {
		return SERVER_BASE + "search/" + productName + "/";
	}
	
	/**
	 * Url for getting all products information by userId
	 * @param userId
	 * @return
	 */
	public static String GetProductByUserId(Long userId){
		return SERVER_BASE + "get/" + userId + "/";
	}
	
	
	/**
	 * Url for analysing the Picture. 
	 * @param filename
	 * @return The text in the picture, normally, it's the name of the product.
	 */
	public static String analysePictureUrl(String filename) {
		return SERVER_BASE + "convert/" + filename + ".jpg" + "/";
	}
	
	/**
	 * Url for downloading the Picture. 
	 * @param filename
	 */
	public static String downloadPictureUrl(String filename) {
		return SERVER_BASE + "uploads/" + filename + ".jpg" + "/";
	}
	
	/**
	 * Url for deleting a product in the server.
	 * parameter format : .../delete/1;3;5
	 * @return
	 */
	public static String deleteProductUrl(String goodsIdList){
		return SERVER_BASE + "delete/" + goodsIdList + "/";
	}
	
	/**
	 * Url for getting the optimized route from server
	 * parameter format: "start-point;end-point;mediate points" (in order)
	 * @param points
	 * @return
	 */
	public static String getRouteUrl(List<LatLng> points){
		String returnUrl = SERVER_BASE + "getpath/";
		for(LatLng point : points){
			String pointX = point.latitude+",";
			String pointY = point.longitude+";";
			returnUrl += (pointX + pointY);
		}
		returnUrl = returnUrl.substring(0, returnUrl.length()-1);
		return returnUrl + "/";
	}
	
	/**
	 * Url for getting the optimized route from server
	 * parameter format: "start-address-name;end-address-name;mediate-address-name" (in order)
	 * @param points
	 * @return
	 */
	public static String getRouteUrlByName(List<String> pointsName){
		String returnUrl = SERVER_BASE + "getpath/";
		for(String pointName : pointsName){
			returnUrl += (pointName + ";");
		}
		return returnUrl + "/";
	}
	
}
