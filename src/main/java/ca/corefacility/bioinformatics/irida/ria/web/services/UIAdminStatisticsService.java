package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.StatisticTimePeriod;
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

	public UIAdminStatisticsService(ProjectService projectService, UserService userService, SampleService sampleService,
			AnalysisSubmissionService analysisSubmissionService) {
		this.projectService = projectService;
		this.userService = userService;
		this.sampleService = sampleService;
		this.analysisSubmissionService = analysisSubmissionService;
	}

	/**
	 * Returns a dto with basic stats (counts) for analyses, projects, users, and samples.
	 *
	 * @param timePeriod - The default time period for which to get the stats for
	 * @return a {@link BasicStats} containing the counts for the time period.
	 */
	public BasicStats getAdminStatistics(Integer timePeriod) {
		Date minimumCreatedDate = new DateTime(new Date()).minusDays(timePeriod)
				.toDate();

		Long analysesRan = analysisSubmissionService.getAnalysesRanInTimePeriod(minimumCreatedDate);
		Long projectsCreated = projectService.getProjectsCreated(minimumCreatedDate);
		Long samplesCreated = sampleService.getSamplesCreated(minimumCreatedDate);
		Long usersCreated = userService.getUsersCreatedInTimePeriod(minimumCreatedDate);
		Long usersLoggedIn = userService.getUsersLoggedIn(minimumCreatedDate);

		// These stats below are used in the UI by tiny charts which require just the values from the objects
		List<Long> analysesCounts = getAdminAnalysesStatistics(timePeriod).getAnalysesStats()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> projectCounts = getAdminProjectStatistics(timePeriod).getProjectStats()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> sampleCounts = getAdminSampleStatistics(timePeriod).getSampleStats()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> userCounts = getAdminUserStatistics(timePeriod).getUserStats()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		return new BasicStats(analysesRan, projectsCreated, samplesCreated, usersCreated, usersLoggedIn, analysesCounts,
				projectCounts, sampleCounts, userCounts);
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the analyses ran counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link AnalysesStatsResponse} containing the counts and labels for the time period.
	 */
	public AnalysesStatsResponse getAdminAnalysesStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new AnalysesStatsResponse(analysisSubmissionService.getAnalysesRanDaily(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new AnalysesStatsResponse(analysisSubmissionService.getAnalysesRanMonthly(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new AnalysesStatsResponse(analysisSubmissionService.getAnalysesRanYearly(minimumCreatedDate));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new AnalysesStatsResponse(analysisSubmissionService.getAnalysesRanHourly(minimumCreatedDate));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the projects created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link ProjectStatsResponse} containing the counts and labels for the time period.
	 */
	public ProjectStatsResponse getAdminProjectStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new ProjectStatsResponse(projectService.getProjectsCreatedDaily(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new ProjectStatsResponse(projectService.getProjectsCreatedMonthly(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new ProjectStatsResponse(projectService.getProjectsCreatedYearly(minimumCreatedDate));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new ProjectStatsResponse(projectService.getProjectsCreatedHourly(minimumCreatedDate));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the samples created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link SampleStatsResponse} containing the counts and labels for the time period.
	 */
	public SampleStatsResponse getAdminSampleStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new SampleStatsResponse(sampleService.getSamplesCreatedDaily(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new SampleStatsResponse(sampleService.getSamplesCreatedMonthly(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new SampleStatsResponse(sampleService.getSamplesCreatedYearly(minimumCreatedDate));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new SampleStatsResponse(sampleService.getSamplesCreatedHourly(minimumCreatedDate));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the users created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link UserStatsResponse} containing the counts and labels for the time period.
	 */
	public UserStatsResponse getAdminUserStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new UserStatsResponse(userService.getUsersCreatedDaily(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new UserStatsResponse(userService.getUsersCreatedMonthly(minimumCreatedDate));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new UserStatsResponse(userService.getUsersCreatedYearly(minimumCreatedDate));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new UserStatsResponse(userService.getUsersCreatedHourly(minimumCreatedDate));
		}
	}

}
