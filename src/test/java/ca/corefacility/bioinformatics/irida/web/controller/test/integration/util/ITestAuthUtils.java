package ca.corefacility.bioinformatics.irida.web.controller.test.integration.util;

import static com.jayway.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

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

	static {
		ROLE_TO_USER = new HashMap<>();
		ROLE_TO_USER.put(ROLE_ADMIN, new UsernamePasswordPair("admin", "password1"));
		ROLE_TO_USER.put(ROLE_USER, new UsernamePasswordPair("fbristow", "password1"));
		ROLE_TO_USER.put(ROLE_MANAGER, new UsernamePasswordPair("manager", "password1"));
	}

	private static RequestSpecification asRole(String role) {
		UsernamePasswordPair pair = ROLE_TO_USER.get(role);
		return given().auth().preemptive().basic(pair.username, pair.password);
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

	private static class UsernamePasswordPair {
		private String username;
		private String password;

		public UsernamePasswordPair(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}
}
