package ca.corefacility.bioinformatics.irida.ria.integration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Use this class as an extension point if you want to use {@link ChromeDriver} to drive the integration test.
 */
public class AbstractIridaUIITChromeDriver extends AbstractIridaUIITPhantomJS {

    @Override
    public WebDriver driverToUse() {
        return new ChromeDriver();
    }
}
