package hk.ust.stop.util;

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
		return SERVER_BASE + "register/" + name + password
				+ email + phone;
	}
}
