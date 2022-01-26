package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectEventEmailScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

public class ProjectEventEmailScheduedTaskImplTest {
	ProjectEventEmailScheduledTaskImpl task;

	@Mock
	UserService userService;

	@Mock
	ProjectEventService eventService;

	@Mock
	ProjectService projectService;

	@Mock
	EmailController emailController;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		task = new ProjectEventEmailScheduledTaskImpl(userService, eventService, projectService, emailController);

		when(emailController.isMailConfigured()).thenReturn(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmailUserTasks() {
		Date priorDateFromCronString = ProjectEventEmailScheduledTaskImpl
				.getPriorDateFromCronString(task.getScheduledCronString());

		User tom = new User("tom", null, null, null, null, null);
		Project p = new Project("testproject");
		List<User> users = Lists.newArrayList(tom);
		ProjectUserJoin join = new ProjectUserJoin(p, tom, ProjectRole.PROJECT_OWNER);
		join.setEmailSubscription(true);
		List<ProjectEvent> events = Lists.newArrayList(new UserRoleSetProjectEvent(join));

		when(userService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(events);
		when(projectService.getProjectsForUser(tom)).thenReturn(Lists.newArrayList(join));

		Date now = new Date();
		task.emailUserTasks();

		verify(userService).getUsersWithEmailSubscriptions();

		ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

		verify(eventService).getEventsForUserAfterDate(eq(tom), dateCaptor.capture());

		verify(emailController).sendSubscriptionUpdateEmail(tom, events);

		Date testedDate = dateCaptor.getValue();

		assertTrue("date should be before current time", now.after(testedDate));

		assertTrue("date should be equal to or before scheduled time",
				priorDateFromCronString.before(testedDate) || priorDateFromCronString.equals(testedDate));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEmailUserPartialSubscriptoinTasks() {
		User tom = new User("tom", null, null, null, null, null);
		Project p = new Project("testproject");
		Project p2 = new Project("testproject2");
		List<User> users = Lists.newArrayList(tom);
		ProjectUserJoin join = new ProjectUserJoin(p, tom, ProjectRole.PROJECT_OWNER);
		join.setEmailSubscription(true);

		ProjectUserJoin notSubscribed = new ProjectUserJoin(p2, tom, ProjectRole.PROJECT_OWNER);

		List<ProjectEvent> events = Lists.newArrayList(new UserRoleSetProjectEvent(join),
				new UserRoleSetProjectEvent(notSubscribed));

		when(userService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(events);
		when(projectService.getProjectsForUser(tom)).thenReturn(Lists.newArrayList(join, notSubscribed));

		task.emailUserTasks();

		verify(userService).getUsersWithEmailSubscriptions();

		verify(eventService).getEventsForUserAfterDate(eq(tom), any(Date.class));

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<List> eventCaptor = ArgumentCaptor.forClass(List.class);
		
		verify(emailController).sendSubscriptionUpdateEmail(eq(tom), eventCaptor.capture());
		
		List<ProjectEvent> sentEvents = eventCaptor.getValue();

		assertEquals("should send 1 event", 1, sentEvents.size());
		
		ProjectEvent sentEvent = sentEvents.iterator().next();
		assertEquals("should have sent from subscribed project", p, sentEvent.getProject());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoTasks() {
		User tom = new User("tom", null, null, null, null, null);
		List<User> users = Lists.newArrayList(tom);

		when(userService.getUsersWithEmailSubscriptions()).thenReturn(users);
		when(eventService.getEventsForUserAfterDate(eq(tom), any(Date.class))).thenReturn(Lists.newArrayList());

		task.emailUserTasks();

		verify(userService).getUsersWithEmailSubscriptions();

		verify(eventService).getEventsForUserAfterDate(eq(tom), any(Date.class));

		verify(emailController, times(0)).sendSubscriptionUpdateEmail(any(User.class), any(List.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoUsers() {
		when(userService.getUsersWithEmailSubscriptions()).thenReturn(Lists.newArrayList());

		task.emailUserTasks();

		verify(userService).getUsersWithEmailSubscriptions();

		verifyNoInteractions(eventService);

		verify(emailController, times(0)).sendSubscriptionUpdateEmail(any(User.class), any(List.class));
	}

}
