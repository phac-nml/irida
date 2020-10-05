package ca.corefacility.bioinformatics.irida.ria.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.*;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAdminStatisticsService;

/**
 * Controller to handle ajax requests for the Admin Panel statistics page.
 */

@RestController
@Scope("session")
@RequestMapping("/ajax/admin")
public class AdminStatisticsAjaxController {

	private UIAdminStatisticsService uiAdminStatisticsService;

	@Autowired
	public AdminStatisticsAjaxController(UIAdminStatisticsService uiAdminStatisticsService) {
		this.uiAdminStatisticsService = uiAdminStatisticsService;
	}

	/**
	 * Get basic usage statistics for projects, samples, analyses, and users
	 * for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve usage stats for
	 * @return dto with usage stats
	 */
	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public ResponseEntity<BasicStats> getAdminStatistics(@RequestParam int timePeriod) {
		return ResponseEntity.ok(uiAdminStatisticsService.getAdminStatistics(timePeriod));
	}

	/**
	 * Get updated usage statistics for projects for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated project usage stats for
	 * @return dto with updated project usage stats
	 */
	@RequestMapping(value = "/project-statistics", method = RequestMethod.GET)
	public ResponseEntity<ProjectStatsResponse> getAdminProjectStatistics(@RequestParam int timePeriod) {
		return ResponseEntity.ok(uiAdminStatisticsService.getAdminProjectStatistics(timePeriod));
	}

	/**
	 * Get updated usage statistics for users for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated user usage stats for
	 * @return dto with updated user usage stats
	 */
	@RequestMapping(value = "/user-statistics", method = RequestMethod.GET)
	public ResponseEntity<UserStatsResponse> getAdminUserStatistics(@RequestParam int timePeriod) {
		return ResponseEntity.ok(uiAdminStatisticsService.getAdminUserStatistics(timePeriod));
	}

	/**
	 * Get updated usage statistics for analyses for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated analyses usage stats for
	 * @return dto with updated analyses usage stats
	 */
	@RequestMapping(value = "/analyses-statistics", method = RequestMethod.GET)
	public ResponseEntity<AnalysesStatsResponse> getAdminAnalysesStatistics(@RequestParam int timePeriod) {
		return ResponseEntity.ok(uiAdminStatisticsService.getAdminAnalysesStatistics(timePeriod));
	}

	/**
	 * Get updated usage statistics for samples for the provided time period
	 *
	 * @param timePeriod The time period for which to retrieve updated sample usage stats for
	 * @return dto with updated sample usage stats
	 */
	@RequestMapping(value = "/sample-statistics", method = RequestMethod.GET)
	public ResponseEntity<SampleStatsResponse> getAdminSampleStatistics(@RequestParam int timePeriod) {
		return ResponseEntity.ok(uiAdminStatisticsService.getAdminSampleStatistics(timePeriod));
	}
}