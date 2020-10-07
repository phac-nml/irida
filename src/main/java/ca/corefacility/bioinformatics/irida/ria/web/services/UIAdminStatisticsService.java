package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
	private int [] DAILY = {7,14,30};
	private int [] MONTHLY = {90, 365};
	private int [] YEARLY = {730, 1825, 3650};

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

		Long analysesRan = analysisSubmissionService.getAnalysesRanInTimePeriod(minimumCreatedDate);
		Long projectsCreated = projectService.getProjectsCreated(minimumCreatedDate);
		Long samplesCreated = sampleService.getSamplesCreated(minimumCreatedDate);
		Long usersLoggedIn = userService.getUsersLoggedIn(minimumCreatedDate);

		return new BasicStats(analysesRan, projectsCreated, samplesCreated, usersLoggedIn);
	}

	public AnalysesStatsResponse getAdminAnalysesStatistics(Integer timePeriod) {
		List<GenericStatModel> analysesList = new ArrayList<>();
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if(IntStream.of(DAILY).anyMatch((x -> x == timePeriod))) {
			analysesList = analysisSubmissionService.getAnalysesRanDaily(minimumCreatedDate);
		} else if(IntStream.of(MONTHLY).anyMatch((x -> x == timePeriod))) {
			analysesList = analysisSubmissionService.getAnalysesRanMonthly(minimumCreatedDate);
		} else if(IntStream.of(YEARLY).anyMatch((x -> x == timePeriod))) {
			analysesList = analysisSubmissionService.getAnalysesRanYearly(minimumCreatedDate);
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			analysesList = analysisSubmissionService.getAnalysesRanHourly(minimumCreatedDate);
		}

		return new AnalysesStatsResponse(analysesList);
	}

	public ProjectStatsResponse getAdminProjectStatistics(Integer timePeriod) {

		List<GenericStatModel> projectsList = new ArrayList<>();
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if(IntStream.of(DAILY).anyMatch((x -> x == timePeriod))) {
			projectsList = projectService.getProjectsCreatedDaily(minimumCreatedDate);
		} else if(IntStream.of(MONTHLY).anyMatch((x -> x == timePeriod))) {
			projectsList = projectService.getProjectsCreatedMonthly(minimumCreatedDate);
		} else if(IntStream.of(YEARLY).anyMatch((x -> x == timePeriod))) {
			projectsList = projectService.getProjectsCreatedYearly(minimumCreatedDate);
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			projectsList = projectService.getProjectsCreatedHourly(minimumCreatedDate);
		}

		return new ProjectStatsResponse(projectsList);

	}

	public SampleStatsResponse getAdminSampleStatistics(Integer timePeriod) {
		return new SampleStatsResponse(null);
	}

	public UserStatsResponse getAdminUserStatistics(Integer timePeriod) {
		return new UserStatsResponse(null);
	}

}
