package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunFilesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunFilesPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunFilesPage page;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SequencingRunFilesPage(driver());
		page.getFilesPage(1L);
	}

	@Test
	public void testGetFiles() {
		List<Map<String, String>> sequenceFiles = page.getSequenceFiles();
		assertEquals(3, sequenceFiles.size());
	}

	@Test
	public void testGetFile() {
		Map<String, String> sequenceFileByRow = page.getSequenceFileByRow(1);
		assertEquals(sequenceFileByRow.get("id"), "2");
		assertEquals(sequenceFileByRow.get("fileName"), "FileThatMayNotExist1");
	}

	@Test
	public void testGetFilesCount() {
		assertEquals(3, page.getFilesCount());
	}
}
