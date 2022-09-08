package ca.corefacility.bioinformatics.irida.web.controller.test.integration.util;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;

import java.util.HashMap;
import java.util.Map;

import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Utilities for doing web requests as certain types of user roles.
 */
public class ITestAuthUtils {

	private static final Map<String, AuthenticationHolder> ROLE_TO_USER;

	private static final String ROLE_USER = "user";
	private static final String ROLE_MANAGER = "manager";
	private static final String ROLE_ADMIN = "admin";
	private static final String ROLE_SEQUENCER = "sequencer";
	private static final String ROLE_OTHER_USER = "other";
	public static final String BAD_USERNAME = "bad_username";
	public static final String BAD_PASSWORD = "bad_password";

	private static final String CLIENT_ID = "testClient";
	private static final String CLIENT_SECRET = "testClientSecret";
	private static final String OAUTH_ENDPOINT = "/api/oauth/token";

	static {
		ROLE_TO_USER = new HashMap<>();
		ROLE_TO_USER.put(ROLE_ADMIN, new AuthenticationHolder("admin", "password1"));
		ROLE_TO_USER.put(ROLE_USER, new AuthenticationHolder("fbristow", "password1"));
		ROLE_TO_USER.put(ROLE_MANAGER, new AuthenticationHolder("manager", "password1"));
		ROLE_TO_USER.put(ROLE_SEQUENCER, new AuthenticationHolder("uploader", "password1"));
		ROLE_TO_USER.put(ROLE_OTHER_USER, new AuthenticationHolder("other", "password1"));
		ROLE_TO_USER.put(BAD_USERNAME, new AuthenticationHolder("bad", "bad"));
		ROLE_TO_USER.put(BAD_PASSWORD, new AuthenticationHolder("admin", "bad"));
	}

	public static RequestSpecification asRole(String role) {
		AuthenticationHolder pair = ROLE_TO_USER.get(role);
		String oAuthToken = getOAuthToken(pair.username, pair.password);
		pair.setToken(oAuthToken);
		String authString = "Bearer " + oAuthToken;
		return given().header("Authorization", authString);
	}

	public static String getTokenForRole(final String role) {
		AuthenticationHolder pair = ROLE_TO_USER.get(role);
		if (pair.token == null) {
			asRole(role);
		}
		return pair.token;
	}

	private static String getOAuthToken(String username, String password) {
		Response response = given().auth()
				.preemptive()
				.basic(CLIENT_ID, CLIENT_SECRET)
				.contentType("application/x-www-form-urlencoded")
				.formParam("grant_type", "password")
				.formParam("username", username)
				.formParam("password", password)
				.post(OAUTH_ENDPOINT);
		try {
			String token = from(response.getBody().asString()).getString("access_token");
			return token;
		} catch (JsonPathException ex) {
			return "";
		}
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
	 * Execute an HTTP request as a *different* user.
	 * 
	 * @return a {@link RequestSpecification} with user privileges.
	 */
	public static RequestSpecification asOtherUser() {
		return asRole(ROLE_OTHER_USER);
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

	private static class AuthenticationHolder {
		private final String username;
		private final String password;
		private String token;

		public AuthenticationHolder(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public void setToken(final String token) {
			this.token = token;
		}
	}
}
