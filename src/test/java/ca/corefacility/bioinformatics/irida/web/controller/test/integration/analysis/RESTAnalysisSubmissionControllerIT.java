package ca.corefacility.bioinformatics.irida.web.controller.test.integration.analysis;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.web.spring.view.NewickFileView;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/analysis/RESTAnalysisSubmissionControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
/**
 * Test for functions of {@link RESTAnalysisSubmissionController}
 */
public class RESTAnalysisSubmissionControllerIT {

	public static final String ANALYSIS_BASE = "/api/analysisSubmissions";

	@Test
	public void testReadSubmission() {
		asAdmin().expect().body("resource.name", equalTo("another analysis")).and()
				.body("resource.analysisState", equalTo(AnalysisState.COMPLETED.toString()))
				.body("resource.links.rel", hasItems("self", "analysis")).when().get(ANALYSIS_BASE + "/1");
	}

	@Test
	public void testReadIncompleteSubmission() {
		asAdmin().expect().body("resource.analysisState", equalTo(AnalysisState.PREPARING.toString()))
				.body("resource.links.rel", not(hasItems("analysis"))).when().get(ANALYSIS_BASE + "/2");
	}

	@Test
	public void testGetAnalysis() {
		asAdmin().expect().body("resource.executionManagerAnalysisId", equalTo("XYZABC")).and()
				.body("resource.links.rel", hasItems("self", "tree")).when().get(ANALYSIS_BASE + "/1/analysis");
	}

	@Test
	public void testGetOutputFile() {
		asAdmin().expect().body("resource.executionManagerFileId", equalTo("123-456-789")).and()
				.body("resource.label", equalTo("snp_tree.tree")).body("resource.links.rel", hasItems("self")).when()
				.get(ANALYSIS_BASE + "/1/analysis/file/tree");

		// get the tree file
		asAdmin().given().header("Accept", NewickFileView.DEFAULT_CONTENT_TYPE).expect().body(containsString("c6706"))
				.when().get(ANALYSIS_BASE + "/1/analysis/file/tree");
	}
}
