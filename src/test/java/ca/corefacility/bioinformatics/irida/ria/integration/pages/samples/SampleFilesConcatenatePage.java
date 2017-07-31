package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.samples.SampleFilesConcatenatePageIT;

/**
 * Test page for sequence files concatenation page.
 * 
 * @see SampleFilesConcatenatePageIT
 */
public class SampleFilesConcatenatePage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(SampleFilesConcatenatePage.class);

	@FindBy(id = "form-submit")
	private WebElement submitBtn;

	@FindBy(className = "pair_object")
	private List<WebElement> pairs;

	@FindBy(className = "single_object")
	private List<WebElement> singles;

	@FindBy(id = "form-submit")
	private WebElement formSubmit;

	@FindBy(className = "concat")
	private List<WebElement> concatChecks;

	public SampleFilesConcatenatePage(WebDriver driver) {
		super(driver);
	}

	public static SampleFilesConcatenatePage goToConcatenatePage(WebDriver driver, Long sampleId) {
		String url = "samples/" + sampleId + "/sequenceFiles/concatenate";
		logger.trace("Going to concatenate page for sample " + sampleId);
		get(driver, url);
		waitForTime(400);
		return PageFactory.initElements(driver, SampleFilesConcatenatePage.class);
	}

	public void selectSingles() {
		singles.forEach(s -> {
			WebElement concatCheck = s.findElement(By.className("concat"));
			concatCheck.click();
		});
	}

	public void selectPairs() {
		pairs.forEach(s -> {
			WebElement concatCheck = s.findElement(By.className("concat"));
			concatCheck.click();
		});
	}

	public void uncheckAll() {
		concatChecks.forEach(c -> {
			if (c.isSelected()) {
				c.click();
			}
		});
	}

	public long getSelectedCount() {
		return concatChecks.stream().filter(c -> c.isSelected()).count();
	}

	public boolean isSubmitEnabled() {
		return formSubmit.isEnabled();
	}
}
