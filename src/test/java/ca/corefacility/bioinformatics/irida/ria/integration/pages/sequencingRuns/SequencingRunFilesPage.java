package ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class SequencingRunFilesPage extends AbstractPage {
	public static String PAGEURL = "sequencing-runs/";
	private static final Logger logger = LoggerFactory.getLogger(SequencingRunFilesPage.class);

	public SequencingRunFilesPage(WebDriver driver) {
		super(driver);
	}

	public void getFilesPage(Long id) {
		String url = PAGEURL + id + "/sequenceFiles";
		logger.debug("Loading sequencing run files " + id + " at " + url);
		get(driver, url);
	}

	public List<Map<String, String>> getSequenceFiles() {
		List<WebElement> findElements = driver.findElements(By.className("file-row"));

		List<Map<String, String>> files = findElements.stream().map((e) -> {
			Map<String, String> data = new HashMap<>();
			data.put("id", e.findElement(By.className("file-id")).getText());
			data.put("fileName", e.findElement(By.className("file-name")).getText());
			return data;
		}).collect(Collectors.toList());

		return files;
	}

	public Map<String, String> getSequenceFileByRow(int row) {
		return getSequenceFiles().get(row);
	}

	public int getFilesCount() {
		return Integer.parseInt(driver.findElement(By.id("file-count")).getText());
	}
}
