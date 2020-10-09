package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.web.services.UIAdminStatisticsService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;


import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UIAdminStatisticsServiceTest {
	/*
	Mock Data
	 */
	private UIAdminStatisticsService service;
	private ProjectService projectService;
	private UserService userService;
	private SampleService sampleService;
	private AnalysisSubmissionService analysisSubmissionService;
	private Integer defaultTimePeriod = 7;
	private Date minimumCreatedDate;

	@Before
	public void setUp() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		service = new UIAdminStatisticsService(projectService, userService, sampleService, analysisSubmissionService);

		minimumCreatedDate = new DateTime(new Date()).minusDays(defaultTimePeriod)
				.toDate();
	}

	@Test
	public void testBasicStats() {
		Date basicStatsMinimumCreatedDate = new DateTime(new Date()).minusDays(defaultTimePeriod)
				.toDate();
		service.getAdminStatistics(defaultTimePeriod);
		verify(analysisSubmissionService, times(1)).getAnalysesRanInTimePeriod(basicStatsMinimumCreatedDate);
		verify(projectService, times(1)).getProjectsCreated(basicStatsMinimumCreatedDate);
		verify(sampleService, times(1)).getSamplesCreated(basicStatsMinimumCreatedDate);
		verify(userService, times(1)).getUsersCreatedInTimePeriod(basicStatsMinimumCreatedDate);
		verify(userService, times(1)).getUsersLoggedIn(basicStatsMinimumCreatedDate);
	}

	@Test
	public void testAnalysesStats() {
		service.getAdminAnalysesStatistics(defaultTimePeriod);
		verify(analysisSubmissionService, times(1)).getAnalysesRanDaily(minimumCreatedDate);
	}

	@Test
	public void testProjectStats() {
		service.getAdminProjectStatistics(defaultTimePeriod);
		verify(projectService, times(1)).getProjectsCreatedDaily(minimumCreatedDate);
	}

	@Test
	public void testSampleStats() {
		service.getAdminSampleStatistics(defaultTimePeriod);
		verify(sampleService, times(1)).getSamplesCreatedDaily(minimumCreatedDate);
	}

	@Test
	public void testUserStats() {
		service.getAdminUserStatistics(defaultTimePeriod);
		verify(userService, times(1)).getUsersCreatedDaily(minimumCreatedDate);
	}

}
