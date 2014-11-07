package ca.corefacility.bioinformatics.irida.ria.integration.drivers;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Created by josh on 14-11-07.
 */
public class IridaPhantomJSDriver extends PhantomJSDriver {
	public IridaPhantomJSDriver() {
		this.manage().window().setSize(new Dimension(1024, 900));
		this.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
}
