package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserStatisticsResponse;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * UI Service for handling requests related to user statistics
 */

@Component
public class UIUserStatisticsService {

	private UserService userService;
	private ProjectService projectService;
	private SampleService sampleService;
	private AnalysisSubmissionService analysisSubmissionService;

	public UIUserStatisticsService(UserService userService, ProjectService projectService, SampleService sampleService,
			AnalysisSubmissionService analysisSubmissionService) {
		this.userService = userService;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.analysisSubmissionService = analysisSubmissionService;
	}

	/**
	 * Get basic user usage statistics for dashboard
	 *
	 * @param userId The identifier for the user
	 * @return dto with user usage stats
	 */
	@GetMapping("/")
	public UserStatisticsResponse getUserStatistics(Long userId) {
		User user = userService.read(userId);

		// Get Number of Projects user is on
		List<Project> projectList = projectService.getProjectsForUserUnique(user);
		int numberOfProjects = projectList.size();

		// Get Number of Samples for user through projects
		int numberOfSamples = 0;

		for (Project project : projectList) {
			numberOfSamples += sampleService.getNumberOfSamplesForProject(project);
		}

		// Get Number of Analyses ran by user
		int numberOfAnalyses = analysisSubmissionService.getNumberAnalysesByUser(user);

		return new UserStatisticsResponse(numberOfProjects, numberOfSamples, numberOfAnalyses);
	}
}
