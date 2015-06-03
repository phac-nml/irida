package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunFilesPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
public class SequencingRunFilesPageIT extends AbstractIridaUIITChromeDriver {
	private SequencingRunFilesPage page;

	@Before
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
		assertEquals("2", sequenceFileByRow.get("id"));
		assertEquals("FileThatMayNotExist1", sequenceFileByRow.get("fileName"));
	}

	@Test
	public void testGetFilesCount() {
		assertEquals(3, page.getFilesCount());
	}
}
