package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Date;
import java.util.List;
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

	/**
	 * Returns a dto with basic stats (counts) for analyses, projects, users, and samples.
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link BasicStats} containing the counts for the time period.
	 */
	public BasicStats getAdminStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		Long analysesRan = analysisSubmissionService.getAnalysesRanInTimePeriod(minimumCreatedDate);
		Long projectsCreated = projectService.getProjectsCreated(minimumCreatedDate);
		Long samplesCreated = sampleService.getSamplesCreated(minimumCreatedDate);
		Long usersCreated = userService.getUsersCreatedInTimePeriod(minimumCreatedDate);
		Long usersLoggedIn = userService.getUsersLoggedIn(minimumCreatedDate);

		return new BasicStats(analysesRan, projectsCreated, samplesCreated, usersCreated, usersLoggedIn);
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the analyses ran counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link AnalysesStatsResponse} containing the counts and labels for the time period.
	 */
	public AnalysesStatsResponse getAdminAnalysesStatistics(Integer timePeriod) {
		List<GenericStatModel> analysesList;
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

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the projects created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link ProjectStatsResponse} containing the counts and labels for the time period.
	 */
	public ProjectStatsResponse getAdminProjectStatistics(Integer timePeriod) {

		List<GenericStatModel> projectsList;
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

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the samples created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link SampleStatsResponse} containing the counts and labels for the time period.
	 */
	public SampleStatsResponse getAdminSampleStatistics(Integer timePeriod) {
		List<GenericStatModel> samplesList;
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if(IntStream.of(DAILY).anyMatch((x -> x == timePeriod))) {
			samplesList = sampleService.getSamplesCreatedDaily(minimumCreatedDate);
		} else if(IntStream.of(MONTHLY).anyMatch((x -> x == timePeriod))) {
			samplesList = sampleService.getSamplesCreatedMonthly(minimumCreatedDate);
		} else if(IntStream.of(YEARLY).anyMatch((x -> x == timePeriod))) {
			samplesList = sampleService.getSamplesCreatedYearly(minimumCreatedDate);
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			samplesList = sampleService.getSamplesCreatedHourly(minimumCreatedDate);
		}

		return new SampleStatsResponse(samplesList);
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the users created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link UserStatsResponse} containing the counts and labels for the time period.
	 */
	public UserStatsResponse getAdminUserStatistics(Integer timePeriod) {
		List<GenericStatModel> usersList;
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if(IntStream.of(DAILY).anyMatch((x -> x == timePeriod))) {
			usersList = userService.getUsersCreatedDaily(minimumCreatedDate);
		} else if(IntStream.of(MONTHLY).anyMatch((x -> x == timePeriod))) {
			usersList = userService.getUsersCreatedMonthly(minimumCreatedDate);
		} else if(IntStream.of(YEARLY).anyMatch((x -> x == timePeriod))) {
			usersList = userService.getUsersCreatedYearly(minimumCreatedDate);
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			usersList = userService.getUsersCreatedHourly(minimumCreatedDate);
		}

		return new UserStatsResponse(usersList);
	}

}
