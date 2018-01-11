package ca.corefacility.bioinformatics.irida.ria.integration.pages.samples;

import java.util.List;

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

	@FindBy(className = "concat_pair")
	private List<WebElement> pairs;

	@FindBy(className = "concat_single")
	private List<WebElement> singles;

	@FindBy(id = "form-submit")
	private WebElement formSubmit;

	public SampleFilesConcatenatePage(WebDriver driver) {
		super(driver);
	}

	public static SampleFilesConcatenatePage goToConcatenatePage(WebDriver driver, Long sampleId) {
		String url = "samples/" + sampleId + "/concatenate";
		logger.trace("Going to concatenate page for sample " + sampleId);
		get(driver, url);
		waitForTime(400);
		return PageFactory.initElements(driver, SampleFilesConcatenatePage.class);
	}

	public void selectSingles() {
		singles.forEach(s -> {
			s.click();
		});
	}

	public void selectPairs() {
		pairs.forEach(s -> {
			s.click();
		});
	}

	public void uncheckAll() {
		pairs.forEach(c -> {
			if (c.isSelected()) {
				c.click();
			}
		});
		
		singles.forEach(c -> {
			if (c.isSelected()) {
				c.click();
			}
		});
	}

	public long getSelectedCount() {
		return pairs.stream().filter(WebElement::isSelected).count()
				+ singles.stream().filter(WebElement::isSelected).count();
	}

	public boolean isSubmitEnabled() {
		return formSubmit.isEnabled();
	}
}
