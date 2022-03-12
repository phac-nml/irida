package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for handling {@link ProjectEvent} views
 * 
 *
 */
@Controller
@RequestMapping("/events")
public class ProjectEventsController {
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectEventsController(ProjectService projectService,
			UserService userService, MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Update the subscription status on a {@link Project} for a {@link User}
	 * 
	 * @param userId
	 *            The {@link User} id to update
	 * @param projectId
	 *            the {@link Project} to subscribe to
	 * @param subscribe
	 *            boolean whether to be subscribed to the project or not
	 * @param locale
	 *            locale of the request
	 * @return Map success message if the subscription status was updated
	 */
	@RequestMapping(value = "/projects/{projectId}/subscribe/{userId}", method = RequestMethod.POST)
	public Map<String, String> addSubscription(@PathVariable Long userId, @PathVariable Long projectId,
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

		return ImmutableMap.of("success", "true", "message", message);
	}

}
