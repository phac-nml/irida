package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.StatisticTimePeriod;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.BasicStatsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.StatisticsResponse;
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
		Long usersLoggedIn = getAdminUserLoggedInStatistics(timePeriod);

		// These stats below are used in the UI by tiny charts which require just the values from the objects
		List<GenericStatModel> analysesCounts = getAdminAnalysesStatistics(timePeriod).getStatistics();

		List<GenericStatModel> projectCounts = getAdminProjectStatistics(timePeriod).getStatistics();

		List<GenericStatModel> sampleCounts = getAdminSampleStatistics(timePeriod).getStatistics();

		List<GenericStatModel> userCounts = getAdminUserStatistics(timePeriod).getStatistics();

		return new BasicStatsResponse(usersLoggedIn, analysesCounts, projectCounts, sampleCounts, userCounts);
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains the analyses ran counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminAnalysesStatistics(Integer timePeriod) {
		Date minimumCreatedDate = getMinimumCreatedDate(timePeriod);
		StatisticTimePeriod statisticTimePeriod = getStatisticTimePeriod(timePeriod);

		return new StatisticsResponse(
				analysisSubmissionService.getAnalysesRanGrouped(minimumCreatedDate, statisticTimePeriod));
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains the projects created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminProjectStatistics(Integer timePeriod) {
		Date minimumCreatedDate = getMinimumCreatedDate(timePeriod);
		StatisticTimePeriod statisticTimePeriod = getStatisticTimePeriod(timePeriod);

		return new StatisticsResponse(
				projectService.getProjectsCreatedGrouped(minimumCreatedDate, statisticTimePeriod));
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains the samples created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminSampleStatistics(Integer timePeriod) {
		Date minimumCreatedDate = getMinimumCreatedDate(timePeriod);
		StatisticTimePeriod statisticTimePeriod = getStatisticTimePeriod(timePeriod);

		return new StatisticsResponse(sampleService.getSamplesCreatedGrouped(minimumCreatedDate, statisticTimePeriod));
	}

	/**
	 * Returns a dto with a list of {@link GenericStatModel}s which contains the users created counts and labels
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return a {@link StatisticsResponse} containing the counts and labels for the time period.
	 */
	public StatisticsResponse getAdminUserStatistics(Integer timePeriod) {
		Date minimumCreatedDate = getMinimumCreatedDate(timePeriod);
		StatisticTimePeriod statisticTimePeriod = getStatisticTimePeriod(timePeriod);

		return new StatisticsResponse(userService.getUsersCreatedGrouped(minimumCreatedDate, statisticTimePeriod));
	}

	/**
	 * Returns the number of users logged in for the time period
	 *
	 * @param timePeriod - The time period for which to get the stats for
	 * @return {@link Long} number of users logged in during time period
	 */
	public Long getAdminUserLoggedInStatistics(Integer timePeriod) {
		Date minimumCreatedDate = getMinimumCreatedDate(timePeriod);
		return userService.getUsersLoggedIn(minimumCreatedDate);
	}

	/**
	 * Get the StatisticTimePeriod enum for the time period which contains the group by format for the statistics. Will
	 * return DAILY as default.
	 *
	 * @param timePeriod The time period of the statistics
	 * @return {@link StatisticTimePeriod} enum
	 */
	private StatisticTimePeriod getStatisticTimePeriod(Integer timePeriod) {
		for (StatisticTimePeriod stp : StatisticTimePeriod.values()) {
			boolean found = IntStream.of(stp.getValues()).anyMatch((x -> x == timePeriod));
			if (found) {
				return stp;
			}
		}
		return StatisticTimePeriod.DAILY;
	}

	/**
	 * Get the minimum created date depending on the time period provided
	 *
	 * @param timePeriod The time period of the statistics
	 * @return minimum created date for statistics
	 */
	private Date getMinimumCreatedDate(Integer timePeriod) {
		Date currDate = new Date();
		if (timePeriod == 1) {
			return new DateTime(currDate).minusHours(24).toDate();
		} else {
			return new DateTime(currDate).minusDays(timePeriod).toDate();
		}
	}

}
