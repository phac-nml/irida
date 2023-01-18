package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for an analysis service methods for getting analysis output file info for projects and users.
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionServiceIT_getAnalysisOutputFileInfo.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionServiceImpl_getAnalysisOutputFileInfoIT {

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithMockUser(username = "thisguy", roles = "USER")
	public void testGetAllAnalysisOutputInfoSharedWithAProject() throws ParseException {
		final List<ProjectSampleAnalysisOutputInfo> infos = analysisSubmissionService
				.getAllAnalysisOutputInfoSharedWithProject(1L);
		assertEquals(2L, infos.size(),
				"There should be 2 ProjectSampleAnalysisOutputInfo, but there were " + infos.size());
		assertEquals(new HashSet<>(infos), expectedSharedOutputsForProject1(), "All outputs must match expected");
	}

	@Test
	@WithMockUser(username = "otherguy", roles = "USER")
	public void testGetAllAutomatedAnalysisOutputInfoForAProject() throws ParseException {
		final List<ProjectSampleAnalysisOutputInfo> infos = analysisSubmissionService
				.getAllAutomatedAnalysisOutputInfoForAProject(1L);
		assertEquals(4L, infos.size(),
				"There should be 4 ProjectSampleAnalysisOutputInfo, but there were " + infos.size());
		assertEquals(new HashSet<>(infos), expectedAutomatedOutputsForProject1(), "All outputs must match expected");
	}

	private Set<ProjectSampleAnalysisOutputInfo> expectedUserOutputs() throws ParseException {
		final Date date = getDate();

		return ImmutableSet.of(
				new ProjectSampleAnalysisOutputInfo(2L, "sample2", 4L, "sistr", "sistr2.json", 4L,
						BuiltInAnalysisTypes.SISTR_TYPING, UUID.fromString("f73cbfd2-5478-4c19-95f9-690f3712f84d"),
						date, "not sharing my sistr", 4L, null, null, null, 1L),
				new ProjectSampleAnalysisOutputInfo(4L, "sample3", 8L, "sistr", "sistr8.json", 8L,
						BuiltInAnalysisTypes.SISTR_TYPING, UUID.fromString("f73cbfd2-5478-4c19-95f9-690f3712f84d"),
						date, "not sharing my sistr 8", 8L, null, null, null, 1L));
	}

	@Test
	@WithMockUser(username = "otherguy", roles = "USER")
	public void testGetUserAnalysisOutputInfo() throws ParseException {
		final User user = userRepository.loadUserByUsername("otherguy");
		final List<ProjectSampleAnalysisOutputInfo> infos = analysisSubmissionService
				.getAllUserAnalysisOutputInfo(user);
		assertEquals(2L, infos.size(),
				"There should be 2 ProjectSampleAnalysisOutputInfo, but there were " + infos.size());
		assertEquals(new HashSet<>(infos), expectedUserOutputs(), "All outputs must match expected");
	}

	private Set<ProjectSampleAnalysisOutputInfo> expectedSharedOutputsForProject1() throws ParseException {
		final Date date = getDate();
		return ImmutableSet.of(new ProjectSampleAnalysisOutputInfo(1L, "sample1", 1L, "contigs", "contigs.fasta", 1L,
				BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION, UUID.fromString("92ecf046-ee09-4271-b849-7a82625d6b60"), date,
				"sub1", 1L, 2L, "This", "Guy", 1L),
				new ProjectSampleAnalysisOutputInfo(2L, "sample2", 1L, "contigs", "contigs.fasta", 1L,
						BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION,
						UUID.fromString("92ecf046-ee09-4271-b849-7a82625d6b60"), date, "sub1", 1L, 2L, "This", "Guy",
						1L));
	}

	private Set<ProjectSampleAnalysisOutputInfo> expectedAutomatedOutputsForProject1() throws ParseException {
		final Date date = getDate();
		return ImmutableSet.of(new ProjectSampleAnalysisOutputInfo(1L, "sample1", 6L, "contigs", "contigs6.fasta", 6L,
				BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION, UUID.fromString("92ecf046-ee09-4271-b849-7a82625d6b60"), date,
				"auto assembly 6", 6L, 1L, "Ad", "Min", 1L),
				new ProjectSampleAnalysisOutputInfo(1L, "sample1", 7L, "sistr", "sistr7.json", 7L,
						BuiltInAnalysisTypes.SISTR_TYPING, UUID.fromString("f73cbfd2-5478-4c19-95f9-690f3712f84d"),
						date, "auto sistr 7", 7L, 1L, "Ad", "Min", 1L),
				new ProjectSampleAnalysisOutputInfo(2L, "sample2", 2L, "contigs", "contigs2.fasta", 2L,
						BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION,
						UUID.fromString("92ecf046-ee09-4271-b849-7a82625d6b60"), date, "auto assembly", 2L, 1L, "Ad",
						"Min", 1L),
				new ProjectSampleAnalysisOutputInfo(2L, "sample2", 3L, "sistr", "sistr.json", 3L,
						BuiltInAnalysisTypes.SISTR_TYPING, UUID.fromString("f73cbfd2-5478-4c19-95f9-690f3712f84d"),
						date, "auto sistr", 3L, 1L, "Ad", "Min", 1L));
	}

	private Date getDate() throws ParseException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
		return dateFormat.parse("2018-07-04 10:00:00.0");
	}

}
