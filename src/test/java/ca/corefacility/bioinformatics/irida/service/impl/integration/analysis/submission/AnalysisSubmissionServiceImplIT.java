package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

/**
 * Tests for an analysis service.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiNoGalaxyTestConfig.class,
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionServiceImplIT {

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	/**
	 * Tests successfully getting a state for an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetStateForAnalysisSubmissionSuccess() {
		AnalysisState state = analysisSubmissionService.getStateForAnalysisSubmission(1l);
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

	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void searchAnalyses() {

		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification.searchAnalysis(null, null,
				null, null);
		Page<AnalysisSubmission> paged = analysisSubmissionService.search(specification, 0, 10, Sort.Direction.ASC,
				"createdDate");
		assertEquals(8, paged.getContent().size());

		// Try filtering a by names
		String name = "My";
		specification = AnalysisSubmissionSpecification.searchAnalysis(name, null, null, null);
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

	/**
	 * Tests reading a submission as a regular user
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testReadGrantedRegularUser() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		assertNotNull("submission was not properly returned", submission);
	}

	/**
	 * Tests being denied to read a submission as a regular user
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testReadDeniedRegularUser() {
		analysisSubmissionService.read(1L);
	}

	/**
	 * Tests reading a submission as an admin user
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "ADMIN")
	public void testReadSuccessAdmin() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		assertNotNull("submission was not properly returned", submission);
	}
	
	/**
	 * Tests reading multiple submissions as a regular user
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testReadMultipleGrantedRegularUser() {
		Iterable<AnalysisSubmission> submissions = analysisSubmissionService.readMultiple(Sets.newHashSet(1L, 2L));
		Iterator<AnalysisSubmission> submissionIter = submissions.iterator();
		assertNotNull("Should have one submission", submissionIter.next());
		assertNotNull("Should have two submissions", submissionIter.next());
	}
	
	/**
	 * Tests reading multiple submissions as a regular user and being denied.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testReadMultipleDeniedRegularUser() {
		analysisSubmissionService.readMultiple(Sets.newHashSet(1L, 2L));
	}
	
	/**
	 * Tests finding all as a regular user and being denied.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "aaron", roles = "USER")
	public void testFindAllDeniedRegularUser() {
		analysisSubmissionService.findAll();
	}
	
	/**
	 * Tests finding all as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindAllAdminUser() {
		assertNotNull("Should find submissions", analysisSubmissionService.findAll());
	}
	
	/**
	 * Tests checking for existence of an {@link AnalysisSubmission} as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testExistsRegularUser() {
		assertTrue("Submission should exist", analysisSubmissionService.exists(1L));
	}
	
	/**
	 * Tests checking for existence of an {@link AnalysisSubmission} as a regular non-owner user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testExistsRegularNonOwnerUser() {
		assertTrue("Submission should exist", analysisSubmissionService.exists(1L));
	}
	
	/**
	 * Tests finding revisions for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testFindRevisionsRegularUser() {
		assertNotNull("should return revisions exist", analysisSubmissionService.findRevisions(1L));
	}
	
	/**
	 * Tests being denied to find revisions for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testFindRevisionsDeniedUser() {
		analysisSubmissionService.findRevisions(1L);
	}
	
	/**
	 * Tests finding pageable revisions for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testFindRevisionsPageRegularUser() {
		assertNotNull("should return revisions exist", analysisSubmissionService.findRevisions(1L, new PageRequest(1,1)));
	}
	
	/**
	 * Tests being denied to find revisions for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testFindRevisionsPageDeniedUser() {
		analysisSubmissionService.findRevisions(1L, new PageRequest(1,1));
	}
	
	/**
	 * Tests getting state for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetStateForAnalysisSubmissionRegularUser() {
		assertNotNull("state should return successfully", analysisSubmissionService.getStateForAnalysisSubmission(1L));
	}
	
	/**
	 * Tests being denied to get the state for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetStateForAnalysisSubmissionDeniedUser() {
		analysisSubmissionService.getStateForAnalysisSubmission(1L);
	}
	
	/**
	 * Tests listing submissions as the regular user and being denied.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "aaron", roles = "USER")
	public void testListDeniedRegularUser() {
		analysisSubmissionService.list(1,1,Direction.ASC);
	}
	
	/**
	 * Tests listing submissions as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testListAdminUser() {
		assertNotNull("Should list submissions", analysisSubmissionService.list(1,1,Direction.ASC));
	}
	
	/**
	 * Tests listing submissions as the regular user with sort properties and being denied.
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "aaron", roles = "USER")
	public void testListSortPropertiesDeniedRegularUser() {
		analysisSubmissionService.list(1,1,Direction.ASC, "");
	}
	
	/**
	 * Tests listing submissions with sort properties as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testListSortPropertiesAdminUser() {
		assertNotNull("Should list submissions", analysisSubmissionService.list(1,1,Direction.ASC, "submitter"));
	}
}
