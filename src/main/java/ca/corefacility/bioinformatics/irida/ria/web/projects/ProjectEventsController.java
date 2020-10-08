package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.enums.UserGroupRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserGroupRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
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
	public static final String EVENTS_VIEW = "events/events";
	public static final String ADMIN_EVENTS_VIEW = "events/admin";

	public static final Map<Class<? extends ProjectEvent>, String> FRAGMENT_NAMES = 
								new ImmutableMap.Builder<Class<? extends ProjectEvent>, String>()
									.put(UserRoleSetProjectEvent.class, "user-role-event")
									.put(UserRemovedProjectEvent.class, "user-removed-event")
									.put(SampleAddedProjectEvent.class, "sample-added-event")
									.put(SampleRemovedProjectEvent.class, "sample-removed-event")
									.put(DataAddedToSampleProjectEvent.class, "data-added-event")
									.put(UserGroupRoleSetProjectEvent.class, "user-group-role-event")
									.put(UserGroupRemovedProjectEvent.class, "user-group-removed-event")
								.build();
	private static final String DEFAULT_PAGE_SIZE = "10";

	private final ProjectEventService eventService;
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectEventsController(ProjectEventService eventService, ProjectService projectService,
			UserService userService, MessageSource messageSource) {
		this.eventService = eventService;
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Get recent {@link ProjectEvent}s for the given {@link Project}
	 * 
	 * @param projectId
	 *            The ID of the {@link Project} to get events for
	 * @param model
	 *            Model for the view. Contains a list named "events". This will
	 *            be a map which will contain "name" which is the name of the
	 *            view fragment to use, and "event" which is a reference to the
	 *            event itself
	 * @param size
	 *            Number of events to show
	 * @return The name of the events view
	 */
	@RequestMapping("/project/{projectId}")
	public String getRecentEventsForProject(@PathVariable Long projectId, Model model,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
		Project project = projectService.read(projectId);

		Page<ProjectEvent> events = eventService.getEventsForProject(project,
				PageRequest.of(0, size, Direction.DESC, "createdDate"));
		List<Map<String, Object>> eventInfo = buildEventsListFromPage(events);

		model.addAttribute("events", eventInfo);

		return EVENTS_VIEW;
	}

	/**
	 * Get recent {@link ProjectEvent}s for the currently logged in user
	 * 
	 * @param model
	 *            Model for the view. Contains a list named "events". This will
	 *            be a map which will contain "name" which is the name of the
	 *            view fragment to use, and "event" which is a reference to the
	 *            event itself
	 * @param principal
	 *            currently logged in principal
	 * @param size
	 *            Number of events to show
	 * @return The name of the events view
	 */
	@RequestMapping("/current_user")
	public String getRecentEventsForUser(Model model, Principal principal,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
		String userName = principal.getName();
		User user = userService.getUserByUsername(userName);

		Page<ProjectEvent> events = eventService.getEventsForUser(user,
				PageRequest.of(0, size, Direction.DESC, "createdDate"));
		List<Map<String, Object>> eventInfo = buildEventsListFromPage(events);

		model.addAttribute("events", eventInfo);

		return EVENTS_VIEW;
	}

	/**
	 * Return a list of events for all projects
	 * 
	 * @param model
	 *            Model attribute for returned view
	 * @param size
	 *            Number of events to show
	 * @return Name of the events view
	 */
	@RequestMapping("/all")
	public String getAllRecentEvents(Model model,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
		Page<ProjectEvent> list = eventService.list(0, size, Direction.DESC, "createdDate");

		List<Map<String, Object>> eventInfo = buildEventsListFromPage(list);

		model.addAttribute("events", eventInfo);

		return EVENTS_VIEW;
	}

	/**
	 * Get the view name of the admin events page
	 * 
	 * @return View name of the admin events page
	 */
	@RequestMapping("/admin")
	public String getAdminEventsPage() {
		return "events/admin";
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

	/**
	 * Convert the Page of events to the list expected in the model
	 * 
	 * @param events
	 *            Page of {@link ProjectEvent}s
	 * @return A List<Map<String,Object>> containing the events and fragment
	 *         names
	 */
	private List<Map<String, Object>> buildEventsListFromPage(Page<ProjectEvent> events) {
		List<Map<String, Object>> eventInfo = new ArrayList<>();
		for (ProjectEvent e : events) {
			if (FRAGMENT_NAMES.containsKey(e.getClass())) {
				Map<String, Object> info = new HashMap<>();
				info.put("name", FRAGMENT_NAMES.get(e.getClass()));
				info.put("event", e);
				eventInfo.add(info);
			}
		}

		return eventInfo;
	}
}
