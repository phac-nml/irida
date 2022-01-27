package ca.corefacility.bioinformatics.irida.web.controller.test.integration.util;

import com.google.common.base.Strings;

public class ITestSystemProperties {
	public static final String APP_PORT = Strings.isNullOrEmpty(System.getProperty("server.port")) ? "8080" : System.getProperty("server.port");
	public static final String BASE_URL = "http://localhost:" + APP_PORT;
}
