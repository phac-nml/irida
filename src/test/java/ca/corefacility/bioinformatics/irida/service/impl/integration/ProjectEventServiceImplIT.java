package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/ProjectEventServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectEventServiceImplIT {
	@Autowired
	ProjectEventService projectEventService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	SampleService sampleService;

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testAddProjectUser() {
		Project project = projectService.read(1L);
		User user = userService.read(2L);

		projectService.addUserToProject(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);

		Page<ProjectEvent> eventsForProject = projectEventService.getEventsForProject(project, PageRequest.of(0, 10));

		assertEquals(1, eventsForProject.getTotalElements());
		ProjectEvent event = eventsForProject.iterator().next();

		assertTrue(event instanceof UserRoleSetProjectEvent);
		UserRoleSetProjectEvent userEvent = (UserRoleSetProjectEvent) event;
		assertEquals(project, userEvent.getProject());
		assertEquals(user, userEvent.getUser());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testUpdateProjectUser() throws ProjectWithoutOwnerException {
		Project project = projectService.read(1L);
		User user = userService.read(1L);

		projectService.updateUserProjectRole(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);

		Page<ProjectEvent> eventsForProject = projectEventService.getEventsForProject(project, PageRequest.of(0, 10));

		assertEquals(1, eventsForProject.getTotalElements());
		ProjectEvent event = eventsForProject.iterator().next();

		assertTrue(event instanceof UserRoleSetProjectEvent);
		UserRoleSetProjectEvent userEvent = (UserRoleSetProjectEvent) event;
		assertEquals(project, userEvent.getProject());
		assertEquals(user, userEvent.getUser());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testRemoveUser() throws ProjectWithoutOwnerException {
		Project project = projectService.read(1L);
		User user = userService.read(1L);

		projectService.removeUserFromProject(project, user);

		Page<ProjectEvent> eventsForProject = projectEventService.getEventsForProject(project, PageRequest.of(0, 10));

		assertEquals(1, eventsForProject.getTotalElements());
		ProjectEvent event = eventsForProject.iterator().next();

		assertTrue(event instanceof UserRemovedProjectEvent);
		UserRemovedProjectEvent userEvent = (UserRemovedProjectEvent) event;
		assertEquals(project, userEvent.getProject());
		assertEquals(user, userEvent.getUser());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testAddProjectSample() {
		Project project = projectService.read(1L);

		Sample sample = sampleService.read(2L);

		projectService.addSampleToProject(project, sample, true);

		Page<ProjectEvent> eventsForProject = projectEventService.getEventsForProject(project, PageRequest.of(0, 10));

		assertEquals(1, eventsForProject.getTotalElements());
		ProjectEvent event = eventsForProject.iterator().next();

		assertTrue(event instanceof SampleAddedProjectEvent);
		SampleAddedProjectEvent userEvent = (SampleAddedProjectEvent) event;
		assertEquals(project, userEvent.getProject());
		assertEquals(sample, userEvent.getSample());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testErrorThrownNoEvent() {
		Project project = projectService.read(1L);

		Sample sample = sampleService.read(1L);

		try {
			projectService.addSampleToProject(project, sample, true);
			fail("EntityExistsException should have been thrown");
		} catch (EntityExistsException ex) {
			// it's all good
		}

		Page<ProjectEvent> eventsForProject = projectEventService.getEventsForProject(project, PageRequest.of(0, 10));

		assertEquals(0, eventsForProject.getTotalElements(), "No event should be created");
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testGetEventsForProject() {
		Project project1 = projectService.read(1L);
		Project project3 = projectService.read(3L);

		Page<ProjectEvent> eventsForProject1 = projectEventService.getEventsForProject(project1, PageRequest.of(0, 10));
		Page<ProjectEvent> eventsForProject2 = projectEventService.getEventsForProject(project3, PageRequest.of(0, 10));

		assertEquals(0L, eventsForProject1.getTotalElements());
		assertEquals(1L, eventsForProject2.getTotalElements());

		ProjectEvent event2 = eventsForProject2.iterator().next();

		assertEquals(project3, event2.getProject());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testGetEventsForUser() {
		User user1 = userService.read(1L);
		User user2 = userService.read(2L);
		User user3 = userService.read(3L);

		Page<ProjectEvent> events1 = projectEventService.getEventsForUser(user1, PageRequest.of(0, 10));
		Page<ProjectEvent> events2 = projectEventService.getEventsForUser(user2, PageRequest.of(0, 10));
		Page<ProjectEvent> events3 = projectEventService.getEventsForUser(user3, PageRequest.of(0, 10));

		assertEquals(1L, events1.getTotalElements());
		assertEquals(0L, events2.getTotalElements());
		assertEquals(1L, events3.getTotalElements());

		ProjectEvent event1 = events1.iterator().next();
		ProjectEvent event3 = events3.iterator().next();
		assertEquals(event1, event3);

	}

	@WithMockUser(username = "fbristow", password = "password1", roles = "USER")
	@Test
	public void testGetEventsForIndividualUser() {
		User user1 = userService.read(1L);

		Page<ProjectEvent> events1 = projectEventService.getEventsForUser(user1, PageRequest.of(0, 10));

		assertEquals(1L, events1.getTotalElements());
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testGetEventsAfterDate() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date beginning = formatter.parse("2013-07-18 14:00:00");

		User user = userService.read(3L);

		List<ProjectEvent> events = projectEventService.getEventsForUserAfterDate(user, beginning);

		assertEquals(1, events.size(), "1 event should be returned");
	}

	@WithMockUser(username = "tom", password = "password1", roles = "ADMIN")
	@Test
	public void testGetEmptyEventsAfterDate() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date beginning = formatter.parse("2013-07-18 15:00:00");

		User user = userService.read(3L);

		List<ProjectEvent> events = projectEventService.getEventsForUserAfterDate(user, beginning);

		assertTrue(events.isEmpty(), "no events should be found");
	}
}
