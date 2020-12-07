package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.enums.StatisticTimePeriod;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel;
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
	private StatisticTimePeriod statisticTimePeriod = StatisticTimePeriod.DAILY;
	private List<GenericStatModel> genericStatModelList;

	@Before
	public void setUp() {
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		service = new UIAdminStatisticsService(projectService, userService, sampleService, analysisSubmissionService);

		genericStatModelList = mock(List.class);

		minimumCreatedDate = new DateTime(new Date()).minusDays(defaultTimePeriod)
				.toDate();
	}

	@Test
	public void testBasicStats() {
		service.getAdminStatistics(defaultTimePeriod);
		when(service.getAdminAnalysesStatistics(defaultTimePeriod).getStatistics()).thenReturn(genericStatModelList);
		when(service.getAdminProjectStatistics(defaultTimePeriod).getStatistics()).thenReturn(genericStatModelList);
		when(service.getAdminSampleStatistics(defaultTimePeriod).getStatistics()).thenReturn(genericStatModelList);
		when(service.getAdminUserStatistics(defaultTimePeriod).getStatistics()).thenReturn(genericStatModelList);
		when(service.getAdminUserLoggedInStatistics(defaultTimePeriod)).thenReturn(0L);
	}

	@Test
	public void testAnalysesStats() {
		service.getAdminAnalysesStatistics(defaultTimePeriod);
		when(analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate, statisticTimePeriod)).thenReturn(genericStatModelList);
	}

	@Test
	public void testProjectStats() {
		service.getAdminProjectStatistics(defaultTimePeriod);
		when(projectService.getProjectsCreatedGrouped(minimumCreatedDate, statisticTimePeriod)).thenReturn(genericStatModelList);
	}

	@Test
	public void testSampleStats() {
		service.getAdminSampleStatistics(defaultTimePeriod);
		when(sampleService.getSamplesCreatedGrouped(minimumCreatedDate, statisticTimePeriod)).thenReturn(genericStatModelList);
	}

	@Test
	public void testUserStats() {
		service.getAdminUserStatistics(defaultTimePeriod);
		when(userService.getUsersCreatedGrouped(minimumCreatedDate, StatisticTimePeriod.DAILY)).thenReturn(genericStatModelList);
	}

}
