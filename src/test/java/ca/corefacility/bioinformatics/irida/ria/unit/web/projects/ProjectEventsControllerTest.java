package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectEventsController;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectEventsControllerTest {
	private ProjectEventService eventService;
	private ProjectService projectService;
	private UserService userService;
	private MessageSource messageSource;

	private ProjectEventsController controller;

	@BeforeEach
	public void setup() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);

		controller = new ProjectEventsController(projectService, userService, messageSource);
	}

	// @Test
	// public void testGetRecentEventsForUser() {
	// 	ExtendedModelMap model = new ExtendedModelMap();
	// 	ProjectEvent event = new UserRoleSetProjectEvent();
	// 	Page<ProjectEvent> page = new PageImpl<>(Lists.newArrayList(event));
	// 	User user = new User();
	// 	Principal principal = () -> "username";

	// 	when(userService.getUserByUsername(principal.getName())).thenReturn(user);
	// 	when(eventService.getEventsForUser(eq(user), any(Pageable.class))).thenReturn(page);

	// 	String recentEventsForProject = controller.getRecentEventsForUser(model, principal, 10);

	// 	assertEquals(ProjectEventsController.EVENTS_VIEW, recentEventsForProject);
	// 	assertTrue(model.containsAttribute("events"));
	// 	@SuppressWarnings("unchecked")
	// 	List<Map<String, Object>> events = (List<Map<String, Object>>) model.get("events");
	// 	assertEquals(1, events.size());
	// 	Map<String, Object> next = events.iterator().next();
	// 	assertTrue(next.containsKey("name"));
	// 	assertTrue(next.containsKey("event"));
	// 	assertEquals(ProjectEventsController.FRAGMENT_NAMES.get(event.getClass()), next.get("name"));
	// 	assertEquals(event, next.get("event"));
	// }

	// @Test
	// public void testGetAllEvents() {
	// 	ExtendedModelMap model = new ExtendedModelMap();
	// 	ProjectEvent event = new UserRoleSetProjectEvent();
	// 	Page<ProjectEvent> page = new PageImpl<>(Lists.newArrayList(event));
	// 	int size = 10;

	// 	when(eventService.list(0, size, Direction.DESC, "createdDate")).thenReturn(page);

	// 	String recentEventsForProject = controller.getAllRecentEvents(model, 10);

	// 	assertEquals(ProjectEventsController.EVENTS_VIEW, recentEventsForProject);
	// 	assertTrue(model.containsAttribute("events"));

	// 	@SuppressWarnings("unchecked")
	// 	List<Map<String, Object>> events = (List<Map<String, Object>>) model.get("events");
	// 	assertEquals(1, events.size());

	// 	Map<String, Object> next = events.iterator().next();
	// 	assertTrue(next.containsKey("name"));
	// 	assertTrue(next.containsKey("event"));
	// 	assertEquals(ProjectEventsController.FRAGMENT_NAMES.get(event.getClass()), next.get("name"));
	// 	assertEquals(event, next.get("event"));
	// }

	// @Test
	// public void testUnknownEvent() {
	// 	Long projectId = 1L;
	// 	Project project = new Project();
	// 	ProjectEvent event = new ProjectEvent() {

	// 		@Override
	// 		public String getLabel() {
	// 			return "an unmapped event";
	// 		}
	// 	};

	// 	Page<ProjectEvent> page = new PageImpl<>(Lists.newArrayList(event));

	// 	when(projectService.read(projectId)).thenReturn(project);
	// 	when(eventService.getEventsForProject(eq(project), any(Pageable.class))).thenReturn(page);

	// }
}
