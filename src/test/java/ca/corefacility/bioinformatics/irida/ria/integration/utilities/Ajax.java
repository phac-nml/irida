package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Created by josh on 14-06-19.
 */
public class Ajax {
	public static ExpectedCondition<Boolean> waitForAjax(final long timeout) {
		return driver -> {
			final long startTime = System.currentTimeMillis();
			final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;

			while ((startTime + timeout) >= System.currentTimeMillis()) {
				final Boolean scriptResult = (Boolean) javascriptExecutor.executeScript("return jQuery.active == 0");

				if (scriptResult)
					return true;

				delay(100);

			}
			return false;
		};
	}

	private static void delay(final long amount) {
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
