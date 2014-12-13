package hk.ust.stop.util;

import hk.ust.stop.model.UserInformation;


/**
 * This class has two main functions.
 * <p>1. Check whether the name and the password match, if it's matched, 
 * store the information in a UserInformation object.
 * <p>2. Register a new user in the server. If this step is successful, then
 * store the information in a UserInformation object.
 * @author XJR
 */

public class AccountUtil {

	private static UserInformation user;
	private static final String EMPTY_RESPONSE;
	
	static {
		user = null;
		EMPTY_RESPONSE = "{}";
	}
	
	
	/**
	 * Check whether the username and the password match
	 * @param name
	 * @param password
	 * @return
	 */
	public static boolean checkNameAndPassword(String name, String password) {
		String responseData = ConnectionUtil.getFromServer(
				ServerUrlUtil.LoginUrl(name, password));
		if(responseData.equals(EMPTY_RESPONSE))
			return false;
		
		// Change JSON string to a UserInformation object
		user = new UserInformation();
		user.setUserName(
				JsonUtil.jsonObjectTransfer(responseData, "username"));
		user.setUserEmail(
				JsonUtil.jsonObjectTransfer(responseData, "email"));
		user.setUserId(Integer.parseInt(
				JsonUtil.jsonObjectTransfer(responseData, "id")));
		user.setUserPhoneNumber(
				JsonUtil.jsonObjectTransfer(responseData, "phone_number"));
		
		return true;
	}
	
	
	public static boolean isLogin() {
		if(null != user)
			return true;
		else {
			return false;
		}
	}
	
	
	/**
	 * Register a new user
	 * @param name
	 * @param password
	 * @param email
	 * @param phone
	 * @return
	 */
	public static boolean registerNewUser(String name, String password,
								String email, String phone) {
		String responseData = ConnectionUtil.getFromServer(
				ServerUrlUtil.AddUserUrl(name, password, email, phone));
		if(responseData.equals(EMPTY_RESPONSE))
			return false;
		
		// Change JSON string to a UserInformation object
		user = new UserInformation();
		user.setUserName(
				JsonUtil.jsonObjectTransfer(responseData, "username"));
		user.setUserEmail(
				JsonUtil.jsonObjectTransfer(responseData, "email"));
		user.setUserId(Integer.parseInt(
				JsonUtil.jsonObjectTransfer(responseData, "id")));
		user.setUserPhoneNumber(
				JsonUtil.jsonObjectTransfer(responseData, "phone_number"));
		
		return true;
	}
}
