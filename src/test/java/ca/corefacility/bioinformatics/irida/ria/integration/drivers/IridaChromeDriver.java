package ca.corefacility.bioinformatics.irida.ria.integration.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by josh on 14-11-07.
 */
public class IridaChromeDriver extends ChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(IridaChromeDriver.class);
	public IridaChromeDriver() {
		this.manage().window().setSize(new Dimension(1024, 900));
		this.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		logger.warn("***CHROMEDRIVER BEING USED. SHOULD BE REPLACED BY THE PHANTOMJS DRIVER.***");
	}
}
