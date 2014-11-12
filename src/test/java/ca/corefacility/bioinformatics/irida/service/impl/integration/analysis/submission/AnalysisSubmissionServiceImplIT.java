package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionServiceImpl;

/**
 * Tests for an analysis service.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiNoGalaxyTestConfig.class, IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionServiceImplIT {

	@Autowired
	private AnalysisSubmissionServiceImpl analysisSubmissionService;

	/**
	 * Tests successfully getting a state for an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetStateForAnalysisSubmissionSuccess() {
		AnalysisState state = analysisSubmissionService
				.getStateForAnalysisSubmission(1l);
		assertEquals(AnalysisState.SUBMITTING, state);
	}

	/**
	 * Tests failing to get a state for an analysis submission.
	 */
	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetStateForAnalysisSubmissionFail() {
		analysisSubmissionService.getStateForAnalysisSubmission(20l);
	}

	/**
	 * Tests successfully setting the state for an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSetStateForAnalysisSubmissionSuccess() {
		AnalysisSubmission submission = analysisSubmissionService.read(1l);
		assertEquals(AnalysisState.SUBMITTING, submission.getAnalysisState());

		analysisSubmissionService.setStateForAnalysisSubmission(1l,
				AnalysisState.RUNNING);
		submission = analysisSubmissionService.read(1l);
		assertEquals(AnalysisState.RUNNING, submission.getAnalysisState());
	}

	/**
	 * Tests failing to set the state for an analysis submission.
	 */
	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSetStateForAnalysisSubmissionFailing() {
		analysisSubmissionService.setStateForAnalysisSubmission(20l,
				AnalysisState.RUNNING);
	}

	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void searchAnalyses() {

		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification.searchAnalysis(null, null,
				null, null);
		Page<AnalysisSubmission> paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC, "createdDate");
		assertEquals(8, paged.getContent().size());

		// Try filtering a by names
		String name = "My";
		specification = AnalysisSubmissionSpecification.searchAnalysis(name, null,
				null, null);
		paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC, "createdDate");
		assertEquals(7, paged.getContent().size());

		// Add a minDate filter
		Date minDate = new Date(1378479662000L);
		specification = AnalysisSubmissionSpecification.searchAnalysis(name, null, minDate, null);
		paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC, "createdDate");
		assertEquals(6, paged.getContent().size());

		// Add a maxDate filter
		Date maxDate = new Date(1389024062000L);
		specification = AnalysisSubmissionSpecification.searchAnalysis(name, null, minDate, maxDate);
		paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC, "createdDate");
		assertEquals(5, paged.getContent().size());

		// Add a state filter
		AnalysisState state = AnalysisState.COMPLETED;
		specification = AnalysisSubmissionSpecification.searchAnalysis(name, state, minDate, maxDate);
		paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC, "createdDate");
		assertEquals(2, paged.getContent().size());

	}
}
