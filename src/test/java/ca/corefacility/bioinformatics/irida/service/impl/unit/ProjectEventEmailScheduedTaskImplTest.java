package ca.corefacility.bioinformatics.irida.service.impl.unit;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectEventEmailScheduledTaskImpl;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectEventEmailScheduedTaskImplTest {
	ProjectEventEmailScheduledTaskImpl task;

	@Mock
	ProjectEventService eventService;

	@Mock
	ProjectService projectService;

	@Mock
	EmailController emailController;

	@Mock
	ProjectSubscriptionService projectSubscriptionService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		task = new ProjectEventEmailScheduledTaskImpl(eventService, projectService, emailController,
				projectSubscriptionService);

		when(emailController.isMailConfigured()).thenReturn(true);
	}

	@Test
	public void testEmailUserTasks() {
		Date priorDateFromCronString = ProjectEventEmailScheduledTaskImpl.getPriorDateFromCronString(
				task.getScheduledCronString());

		User tom = new User("tom", null, null, null, null, null);
		Project p = new Project("testproject");
		List<User> users = Lists.newArrayList(tom);
		List<Project> projects = Lists.newArrayList(p);
		ProjectUserJoin join = new ProjectUserJoin(p, tom, ProjectRole.PROJECT_OWNER);
		List<ProjectEvent> events = Lists.newArrayList(new UserRoleSetProjectEvent(join));

		when(projectSubscriptionService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(events);
		when(projectSubscriptionService.getProjectsForUserWithEmailSubscriptions(tom)).thenReturn(projects);

		Date now = new Date();
		task.emailUserTasks();

		verify(projectSubscriptionService).getUsersWithEmailSubscriptions();

		ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

		verify(eventService).getEventsForUserAfterDate(eq(tom), dateCaptor.capture());

		verify(emailController).sendSubscriptionUpdateEmail(tom, events);

		Date testedDate = dateCaptor.getValue();

		assertTrue(now.after(testedDate), "date should be before current time");

		assertTrue(priorDateFromCronString.before(testedDate) || priorDateFromCronString.equals(testedDate),
				"date should be equal to or before scheduled time");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmailUserPartialSubscriptionTasks() {
		User tom = new User("tom", null, null, null, null, null);
		Project p = new Project("testproject");
		Project p2 = new Project("testproject2");
		List<User> users = Lists.newArrayList(tom);
		List<Project> projects = Lists.newArrayList(p);
		ProjectUserJoin join = new ProjectUserJoin(p, tom, ProjectRole.PROJECT_OWNER);
		ProjectUserJoin notSubscribed = new ProjectUserJoin(p2, tom, ProjectRole.PROJECT_OWNER);
		List<ProjectEvent> events = Lists.newArrayList(new UserRoleSetProjectEvent(join),
				new UserRoleSetProjectEvent(notSubscribed));

		when(projectSubscriptionService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(events);
		when(projectSubscriptionService.getProjectsForUserWithEmailSubscriptions(tom)).thenReturn(projects);

		task.emailUserTasks();

		verify(projectSubscriptionService).getUsersWithEmailSubscriptions();

		verify(eventService).getEventsForUserAfterDate(eq(tom), any(Date.class));

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<List> eventCaptor = ArgumentCaptor.forClass(List.class);

		verify(emailController).sendSubscriptionUpdateEmail(eq(tom), eventCaptor.capture());

		List<ProjectEvent> sentEvents = eventCaptor.getValue();

		assertEquals(1, sentEvents.size(), "should send 1 event");

		ProjectEvent sentEvent = sentEvents.iterator().next();
		assertEquals(p, sentEvent.getProject(), "should have sent from subscribed project");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoTasks() {
		User tom = new User("tom", null, null, null, null, null);
		List<User> users = Lists.newArrayList(tom);

		when(projectSubscriptionService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(Lists.newArrayList());

		task.emailUserTasks();

		verify(projectSubscriptionService).getUsersWithEmailSubscriptions();

		verify(eventService).getEventsForUserAfterDate(eq(tom), any(Date.class));

		verify(emailController, times(0)).sendSubscriptionUpdateEmail(any(User.class), any(List.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoUsers() {
		when(projectSubscriptionService.getUsersWithEmailSubscriptions()).thenReturn(Lists.newArrayList());

		task.emailUserTasks();

		verify(projectSubscriptionService).getUsersWithEmailSubscriptions();

		verifyNoInteractions(eventService);

		verify(emailController, times(0)).sendSubscriptionUpdateEmail(any(User.class), any(List.class));
	}

}
