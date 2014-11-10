package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

/**
 * This class is used to initial elements needed for integration testing.
 * <p/>
 * Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class TestUtilities {
	/**
	 * Set the defaults for the {@link WebDriver}
	 *
	 * @param driver
	 * 		{@link WebDriver}
	 *
	 * @return A {@link WebDriver} with its defaults set.
	 */
	public static WebDriver setDriverDefaults(WebDriver driver) {
		driver.manage().window().setSize(new Dimension(1024, 900));
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}
}
