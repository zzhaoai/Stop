package hk.ust.stop.util;

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
		return SERVER_BASE + "login/" + name + "/" + password;
	}
	
	
	/**
	 * Return url like this: http://demo.engineerinme.com:5000/product/productId
	 * @param productId
	 * @return
	 */
	public static String ProductByIdUrl(long productId) {
		return SERVER_BASE + "product/" + productId;
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
				+ "/" + phone + "/" + password;
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
				(null == AccountUtil.getLoginUser()?"0":AccountUtil.getLoginUser().getUserId());
	}
	
	
	/**
	 * Url for searching products with the productName.
	 * @param productName
	 * @return
	 */
	public static String SearchProductUrl(String productName) {
		return SERVER_BASE + "search/" + productName;
	}
	
	
	/**
	 * Url for analysing the Picture. 
	 * @param filename
	 * @return The text in the picture, normally, it's the name of the product.
	 */
	public static String analysePictureUrl(String filename) {
		return SERVER_BASE + "convert/" + filename + ".jpg";
	}
	
	/**
	 * Url for downloading the Picture. 
	 * @param filename
	 */
	public static String downloadPictureUrl(String filename) {
		return SERVER_BASE + "uploads/" + filename + ".jpg";
	}
	
}
