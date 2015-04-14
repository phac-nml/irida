package ca.corefacility.bioinformatics.irida.ria.integration.sequencingRuns;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.sequencingRuns.SequencingRunDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/sequencingRuns/SequencingRunsPagesIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequencingRunDetailsPageIT {
	private WebDriver driver;
	private SequencingRunDetailsPage page;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
		LoginPage.loginAsAdmin(driver);
		page = new SequencingRunDetailsPage(driver);
		page.getDetailsPage(1L);
	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testGetDetails() {
		Map<String, String> runDetails = page.getRunDetails();
		assertEquals("A cool run", runDetails.get("Description"));
		assertEquals("test workflow", runDetails.get("Workflow"));
	}

	@Test
	public void testGetSequencerType() {
		String sequencerType = page.getSequencerType();
		assertEquals("MiSeq", sequencerType);
	}
}
