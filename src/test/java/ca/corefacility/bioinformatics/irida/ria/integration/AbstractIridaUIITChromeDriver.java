package ca.corefacility.bioinformatics.irida.ria.integration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Use this class as an extension point if you want to use {@link ChromeDriver} to drive the integration test.
 */
public abstract class AbstractIridaUIITChromeDriver extends AbstractIridaUIITPhantomJS {

    public static WebDriver driverToUse() {
        return new ChromeDriver();
    }
}
