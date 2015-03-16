package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisDetailsPageIT {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);
	private WebDriver driver;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());

	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetUp() throws URISyntaxException, IOException {
		addOutputFile();

		LoginPage.loginAsAdmin(driver);
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver, 4L);

		// Ensure files are displayed
		page.displayFilesView();
		assertEquals("Should be displaying 1 file", 1, page.getNumberOfFilesDisplayed());

		// Ensure tools are displayed
		page.displayTreeTools();
		assertEquals("Should have 2 tools associated with the tree", 2, page.getNumberOfToolsForTree());

		// Ensure the tool parameters can be displayed;
		assertEquals("First tool should have 1 parameter", 1, page.getNumberOfParametersForTool());
	}

	private void addOutputFile() throws URISyntaxException, IOException {
		Path sequenceFilePathReal = Paths
				.get(AnalysisDetailsPageIT.class.getResource("snp_tree.tree").toURI());
		Files.copy(sequenceFilePathReal, Paths.get("/tmp/snp_tree_28329923473842346785384sd2s.tree"),
				StandardCopyOption.REPLACE_EXISTING);

	}
}
