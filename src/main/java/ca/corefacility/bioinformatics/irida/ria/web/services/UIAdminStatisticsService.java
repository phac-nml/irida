package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.*;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * A utility class for formatting responses for the admin statistics page UI.
 */

@Component
public class UIAdminStatisticsService {

	private ProjectService projectService;
	private UserService userService;
	private SampleService sampleService;
	private AnalysisSubmissionService analysisSubmissionService;

	public UIAdminStatisticsService(ProjectService projectService, UserService userService,
			SampleService sampleService, AnalysisSubmissionService analysisSubmissionService) {
		this.projectService = projectService;
		this.userService = userService;

		this.sampleService = sampleService;
		this.analysisSubmissionService = analysisSubmissionService;
	}

	public BasicStats getAdminStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		Long analysesRan = analysisSubmissionService.getAnalysesRan(minimumCreatedDate);
		Long projectsCreated = projectService.getProjectsCreated(minimumCreatedDate);
		Long samplesCreated = sampleService.getSamplesCreated(minimumCreatedDate);
		Long usersLoggedIn = userService.getUsersLoggedIn(minimumCreatedDate);

		return new BasicStats(analysesRan, projectsCreated, samplesCreated, usersLoggedIn);
	}

	public AnalysesStatsResponse getAdminAnalysesStatistics(Integer timePeriod) { return new AnalysesStatsResponse(null); }

	public ProjectStatsResponse getAdminProjectStatistics(Integer timePeriod) {
		return new ProjectStatsResponse(null);
	}

	public SampleStatsResponse getAdminSampleStatistics(Integer timePeriod) {
		return new SampleStatsResponse(null);
	}

	public UserStatsResponse getAdminUserStatistics(Integer timePeriod) {
		return new UserStatsResponse(null);
	}
}
