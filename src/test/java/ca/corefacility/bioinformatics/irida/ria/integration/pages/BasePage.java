package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by josh on 14-08-06.
 */
public class BasePage {
	private static final Logger logger = LoggerFactory.getLogger(BasePage.class);
	public static final String URL = "http://localhost:8080/";
	
	public static void logout(WebDriver driver){
		driver.get(URL+"/logout");
	}

	public static void waitForTime() {
		try {
			// There is a 500 ms pause on filtering names.
			Thread.sleep(700);
		} catch (InterruptedException e) {
			logger.error("Cannot sleep the thread.");
		}
	}
}
