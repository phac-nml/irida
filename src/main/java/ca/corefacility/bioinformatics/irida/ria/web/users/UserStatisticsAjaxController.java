package ca.corefacility.bioinformatics.irida.ria.web.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserStatisticsService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserStatisticsResponse;

/**
 * Controller for user statistics
 */
@RestController
@RequestMapping("/ajax/user/statistics")
public class UserStatisticsAjaxController {

	private UIUserStatisticsService uiUserStatisticsService;

	@Autowired
	public UserStatisticsAjaxController(UIUserStatisticsService uiUserStatisticsService) {
		this.uiUserStatisticsService = uiUserStatisticsService;
	}

	/**
	 * Get basic user usage statistics for dashboard
	 *
	 * @param userId The identifier for the user
	 * @return dto with user usage stats
	 */
	@GetMapping("")
	public ResponseEntity<UserStatisticsResponse> getUserStatistics(@RequestParam Long userId) {
		return ResponseEntity.ok(uiUserStatisticsService.getUserStatistics(userId));
	}
}

