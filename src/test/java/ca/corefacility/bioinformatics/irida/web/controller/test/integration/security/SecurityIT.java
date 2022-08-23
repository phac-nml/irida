package ca.corefacility.bioinformatics.irida.web.controller.test.integration.security;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;

import static io.restassured.RestAssured.expect;

/**
 * General tests relating to security for the REST API.
 */
@RestIntegrationTest
public class SecurityIT {

	/**
	 * Test that we get the right type of response when we don't have valid credentials.
	 */
	@Test
	public void testAccessWithoutAuthentication() {

		expect().statusCode(HttpStatus.SC_UNAUTHORIZED).header("WWW-Authenticate", "Bearer").when().get("/api");
	}
}
