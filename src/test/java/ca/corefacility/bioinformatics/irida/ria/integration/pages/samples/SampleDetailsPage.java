package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p> Page Object to represent the sample details page. </p>
 *
 */
public class SampleDetailsPage extends AbstractPage {
	public static final String RELATIVE_URL = "samples/";

	public SampleDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void gotoPage(Long sampleId) {
		get(driver, RELATIVE_URL + sampleId + "/details");
	}

	public Long getSampleId() {
		return Long.parseLong(driver.findElement(By.id("sb-id")).getText());
	}

	public String getPageTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	public String getCreatedDate() {
		return driver.findElement(By.id("sb-created")).getText();
	}

	public String getOrganismName() {
		return driver.findElement(By.id("dd-organism")).getText();
	}

	public String getStrain() {
		return driver.findElement(By.id("dd-strain")).getText();
	}

	public String getIsolate() {
		return driver.findElement(By.id("dd-isolate")).getText();
	}

	public String getLatitude() {
		return driver.findElement(By.id("dd-latitude")).getText();
	}

	public String getLongitude() {
		return driver.findElement(By.id("dd-longitude")).getText();
	}

	public String getCollectedBy() {
		return driver.findElement(By.id("dd-collectedby")).getText();
	}

	public String getIsolationSource() {
		return driver.findElement(By.id("dd-isolation-source")).getText();
	}

	public String getGeographicLocationName() {
		return driver.findElement(By.id("dd-geo-name")).getText();
	}
}
