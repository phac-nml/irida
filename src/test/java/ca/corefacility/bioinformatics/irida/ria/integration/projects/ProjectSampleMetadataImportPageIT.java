package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSampleMetadataImportPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSampleMetadataView.xml")
public class ProjectSampleMetadataImportPageIT extends AbstractIridaUIITChromeDriver {
	private static final String GOOD_FILE_PATH = "src/test/resources/files/metadata-upload/good.xlsx";
	private static final String MIXED_FILE_PATH = "src/test/resources/files/metadata-upload/mixed.xlsx";

	@Before
	public void init() {
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testGoodFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(GOOD_FILE_PATH);
		page.selectSampleNameColumn();
		assertEquals("Has incorrect amount of rows matching sample names", 5, page.getFoundCount());
		assertEquals("Has incorrect amount of rows missing sample names", 0, page.getMissingCount());

		/*
		Check formatting.  A special check for number column formatting has been added in July 2020.
		Expected: 2.2222 -> 2.22 (if formatting set to 2 decimals). Actual numbers from file, before formatting:
		   2.222222
           2.3666
           1.5689
           63.89756
           59.6666
		 */
		List<Double> values = ImmutableList.of(2.222222, 2.3666, 1.5689, 63.89756, 59.6666)
				.stream()
				.map(num -> BigDecimal.valueOf(num)
						.setScale(2, RoundingMode.HALF_UP)
						.doubleValue())
				.collect(Collectors.toList());
		List<String> formattedNumbers = page.getValuesForColumnByName("Numbers");
		formattedNumbers.forEach(num -> assertTrue("Found " + num + " that was not formatted properly", values.contains(Double.valueOf(num))));

	}

	@Test
	public void testMixedFileAndHeaders() {
		ProjectSampleMetadataImportPage page = ProjectSampleMetadataImportPage.goToPage(driver());
		page.uploadMetadataFile(MIXED_FILE_PATH);
		page.selectSampleNameColumn();
		assertEquals("Has incorrect amount of rows matching sample names", 5, page.getFoundCount());
		assertEquals("Has incorrect amount of rows missing sample names", 2, page.getMissingCount());
	}
}
