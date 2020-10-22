package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.GroupByFormat;
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
	 * @return a {@link BasicStatsResponse} containing the counts for the time period.
	 */
	public BasicStatsResponse getAdminStatistics(Integer timePeriod) {
		Date minimumCreatedDate = new DateTime(new Date()).minusDays(timePeriod)
				.toDate();

		Long usersLoggedIn = userService.getUsersLoggedIn(minimumCreatedDate);

		// These stats below are used in the UI by tiny charts which require just the values from the objects
		List<Long> analysesCounts = getAdminAnalysesStatistics(timePeriod).getStatistics()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> projectCounts = getAdminProjectStatistics(timePeriod).getStatistics()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> sampleCounts = getAdminSampleStatistics(timePeriod).getStatistics()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		List<Long> userCounts = getAdminUserStatistics(timePeriod).getStatistics()
				.stream()
				.map(GenericStatModel::getValue)
				.collect(Collectors.toList());

		return new BasicStatsResponse(usersLoggedIn, analysesCounts, projectCounts, sampleCounts, userCounts);
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the analyses ran counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminAnalysesStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate,
					GroupByFormat.fromString("daily")));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate,
					GroupByFormat.fromString("monthly")));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate,
					GroupByFormat.fromString("yearly")));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new StatisticsResponse(analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate,
					GroupByFormat.fromString("hourly")));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the projects created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminProjectStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					projectService.getProjectsCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("daily")));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					projectService.getProjectsCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("monthly")));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					projectService.getProjectsCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("yearly")));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new StatisticsResponse(
					projectService.getProjectsCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("hourly")));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the samples created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminSampleStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					sampleService.getSamplesCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("daily")));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					sampleService.getSamplesCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("monthly")));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					sampleService.getSamplesCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("yearly")));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new StatisticsResponse(
					sampleService.getSamplesCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("hourly")));
		}
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains
	 * the users created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminUserStatistics(Integer timePeriod) {
		Date currDate = new Date();
		Date minimumCreatedDate = new DateTime(currDate).minusDays(timePeriod)
				.toDate();

		if (IntStream.of(StatisticTimePeriod.DAILY.getDaily())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					userService.getUsersCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("daily")));
		} else if (IntStream.of(StatisticTimePeriod.MONTHLY.getMonthly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					userService.getUsersCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("monthly")));
		} else if (IntStream.of(StatisticTimePeriod.YEARLY.getYearly())
				.anyMatch((x -> x == timePeriod))) {
			return new StatisticsResponse(
					userService.getUsersCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("yearly")));
		} else {
			minimumCreatedDate = new DateTime(currDate).minusHours(24)
					.toDate();
			return new StatisticsResponse(
					userService.getUsersCreatedGrouped(minimumCreatedDate, GroupByFormat.fromString("hourly")));
		}
	}

}
