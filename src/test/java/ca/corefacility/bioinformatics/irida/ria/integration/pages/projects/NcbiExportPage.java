package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.web.util.UriUtils;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Page to test the NCBI export feature
 */
public class NcbiExportPage extends AbstractPage {

	@FindBy(className = "sample-container")
	List<WebElement> samples;

	@FindBy(className = "sample-no-files")
	List<WebElement> disabledSamples;

	@FindBy(className = "bioSample")
	List<WebElement> bioSampleInputs;

	@FindBy(className = "library_construction_protocol")
	List<WebElement> protocolInputs;

	@FindBy(id = "submit")
	WebElement submitButton;

	@FindBy(id = "bioProject")
	WebElement bioProject;

	@FindBy(id = "organization")
	WebElement organization;

	@FindBy(id = "namespace")
	WebElement namespace;

	@FindBy(className = "remove-button")
	List<WebElement> removeButtons;

	public NcbiExportPage(WebDriver driver) {
		super(driver);
	}

	public static NcbiExportPage goTo(WebDriver driver, Long projectId, Collection<Long> sampleIds) {

		StringJoiner stringJoiner = new StringJoiner("&");
		sampleIds.forEach(s -> stringJoiner.add(UriUtils.encodeQuery("ids=" + s.toString(), "UTF-8")));

		String url = "projects/" + projectId + "/export/ncbi?" + stringJoiner.toString();
		get(driver, url);
		return PageFactory.initElements(driver, NcbiExportPage.class);
	}

	public List<String> getSampleNames() {
		return samples.stream().map(s -> s.findElement(By.className("sample-name")).getText())
				.collect(Collectors.toList());
	}

	public int countDisabledSamples() {
		return disabledSamples.size();
	}

	public void fillTopLevelProperties(String bioProject, String organization, String namespace) {
		this.bioProject.sendKeys(bioProject);
		this.organization.sendKeys(organization);
		this.namespace.sendKeys(namespace);
	}

	public void fillSamplesWithInfo(String bioSample, String protocol) {
		bioSampleInputs.forEach(b -> b.sendKeys(bioSample));
		protocolInputs.forEach(c -> c.sendKeys(protocol));
	}

	public void submit() {
		submitButton.click();
	}

	public void removeFirstSample() {
		removeButtons.iterator().next().click();
	}

}
