package ca.corefacility.bioinformatics.irida.web.controller.test.integration.util;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;

import java.util.HashMap;
import java.util.Map;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * Utilities for doing web requests as certain types of user roles.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ITestAuthUtils {

	private static final Map<String, UsernamePasswordPair> ROLE_TO_USER;

	private static final String ROLE_USER = "user";
	private static final String ROLE_MANAGER = "manager";
	private static final String ROLE_ADMIN = "admin";
	private static final String ROLE_SEQUENCER = "sequencer";
	public static final String BAD_USERNAME = "bad_username";
	public static final String BAD_PASSWORD = "bad_password";
	
	private static final String CLIENT_ID = "testClient";
	private static final String CLIENT_SECRET = "testClientSecret";
	private static final String OAUTH_ENDPOINT = "/oauth/token";

	static {
		ROLE_TO_USER = new HashMap<>();
		ROLE_TO_USER.put(ROLE_ADMIN, new UsernamePasswordPair("admin", "password1"));
		ROLE_TO_USER.put(ROLE_USER, new UsernamePasswordPair("fbristow", "password1"));
		ROLE_TO_USER.put(ROLE_MANAGER, new UsernamePasswordPair("manager", "password1"));
		ROLE_TO_USER.put(ROLE_SEQUENCER, new UsernamePasswordPair("uploader", "password1"));
		ROLE_TO_USER.put(BAD_USERNAME, new UsernamePasswordPair("bad", "bad"));
		ROLE_TO_USER.put(BAD_PASSWORD, new UsernamePasswordPair("admin", "bad"));
	}

	public static RequestSpecification asRole(String role) {
		UsernamePasswordPair pair = ROLE_TO_USER.get(role);
		String oAuthToken = getOAuthToken(pair.username,pair.password);
		String authString = "Bearer " + oAuthToken;
		return given().header("Authorization", authString);
	}
	
	private static String getOAuthToken(String username, String password){
		Response response = given().param("grant_type", "password")
			.param("client_id", CLIENT_ID)
			.param("client_secret", CLIENT_SECRET)
			.param("username", username)
			.param("password", password)
			.get(OAUTH_ENDPOINT);
		
		String token = from(response.getBody().asString()).getString("access_token");
		return token;
	}

	/**
	 * Execute an HTTP request as a user.
	 * 
	 * @return a {@link RequestSpecification} with user privileges.
	 */
	public static RequestSpecification asUser() {
		return asRole(ROLE_USER);
	}

	/**
	 * Execute an HTTP request as a manager.
	 * 
	 * @return a {@link RequestSpecification} with manager privileges.
	 */
	public static RequestSpecification asManager() {
		return asRole(ROLE_MANAGER);
	}

	/**
	 * Execute an HTTP request as an admin.
	 * 
	 * @return a {@link RequestSpecification} with admin privileges.
	 */
	public static RequestSpecification asAdmin() {
		return asRole(ROLE_ADMIN);
	}
	
	/**
	 * Execute an HTTP request as a sequencer.
	 * 
	 * @return a {@link RequestSpecification} with admin privileges.
	 */
	public static RequestSpecification asSequencer() {
		return asRole(ROLE_SEQUENCER);
	}

	private static class UsernamePasswordPair {
		private String username;
		private String password;

		public UsernamePasswordPair(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}
}
