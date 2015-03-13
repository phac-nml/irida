package ca.corefacility.bioinformatics.irida.ria.integration.analysis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis.AnalysisDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class, IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/analysis/AnalysisAdminView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisDetailsPageIT {
	private WebDriver driver;

	@Autowired
	private SequenceFileRepository sequenceFileRepository;

	@Autowired
	private AnalysisOutputFileRepository analysisOutputFileRepository;

	@Autowired
	private AnalysisRepository analysisRepository;

	@Before
	public void setUp() {
		driver = TestUtilities.setDriverDefaults(new ChromeDriver());

	}

	@After
	public void destroy() {
		driver.quit();
	}

	@Test
	public void testPageSetUp() throws URISyntaxException {
		LoginPage.loginAsAdmin(driver);
		Analysis analysis = createAnalysis();
		AnalysisDetailsPage page = AnalysisDetailsPage.initPage(driver, 4);
		String fred = "FRED";
	}

	private Analysis createAnalysis() throws URISyntaxException {
		Path sequenceFilePathReal = Paths
				.get(AnalysisDetailsPageIT.class.getResource("snp_tree.tree").toURI());

		SequenceFile file1 = sequenceFileRepository.findOne(1L);
		AnalysisOutputFile outputFile = new AnalysisOutputFile(sequenceFilePathReal, "");
		AnalysisOutputFile newOutputFile = analysisOutputFileRepository.save(outputFile);

		Analysis analysisA = new Analysis(Sets.newHashSet(file1), "", ImmutableMap.of("tree", newOutputFile));
		return analysisRepository.save(analysisA);
	}
}
