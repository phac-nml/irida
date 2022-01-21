package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for handling all AJAX requests for the {@link ProjectEvent} UIs
 */
@RestController
@RequestMapping("/ajax/events")
public class ProjectEventsAjaxController {
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectEventsAjaxController(ProjectService projectService, UserService userService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Update the subscription status on a {@link Project} for a {@link User}
	 *
	 * @param userId    The {@link User} id to update
	 * @param projectId the {@link Project} to subscribe to
	 * @param subscribe boolean whether to be subscribed to the project or not
	 * @param locale    locale of the request
	 * @return Map success message if the subscription status was updated
	 */
	@RequestMapping(value = "/projects/{projectId}/subscribe/{userId}", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> addSubscription(@PathVariable Long userId, @PathVariable Long projectId,
			@RequestParam boolean subscribe, Locale locale) {
		User user = userService.read(userId);
		Project project = projectService.read(projectId);

		userService.updateEmailSubscription(user, project, subscribe);

		String message;
		if (subscribe) {
			message = messageSource.getMessage("user.projects.subscriptions.added", new Object[] { project.getLabel() },
					locale);
		} else {
			message = messageSource.getMessage("user.projects.subscriptions.removed",
					new Object[] { project.getLabel() }, locale);
		}

		return ResponseEntity.ok(new AjaxSuccessResponse(message));
	}
}
