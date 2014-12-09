package ca.corefacility.bioinformatics.irida.web.controller.test.integration.util;

public class ITestSystemProperties {
	public static final String APP_PORT = System.getProperty("jetty.port");
	public static final String BASE_URL = "http://localhost:" + APP_PORT;
}
