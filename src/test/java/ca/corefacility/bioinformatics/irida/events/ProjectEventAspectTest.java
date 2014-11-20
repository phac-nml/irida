package ca.corefacility.bioinformatics.irida.events;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;

public class ProjectEventAspectTest {
	private ProjectEventAspect projectEventAspect;
	private ProjectEventRepository eventRepository;

	private Project project;

	@Before
	public void setup() {
		eventRepository = mock(ProjectEventRepository.class);
		projectEventAspect = new ProjectEventAspect(eventRepository);

		project = new Project("Test project");
	}

	@Test
	public void testProcessProjectUserRoleSet() {
		User user = new User("tom", null, null, "Some", "guy", null);
		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER);

		projectEventAspect.processProjectUserRoleSet(projectUserJoin);

		ArgumentCaptor<UserRoleSetProjectEvent> captor = ArgumentCaptor.forClass(UserRoleSetProjectEvent.class);
		verify(eventRepository).save(captor.capture());

		UserRoleSetProjectEvent value = captor.getValue();
		assertEquals(project, value.getProject());
		assertEquals(user, value.getUser());
	}

	@Test
	public void testProcessSampleAdded() {
		Sample sample = new Sample("test sample");
		ProjectSampleJoin projectSampleJoin = new ProjectSampleJoin(project, sample);

		projectEventAspect.processSampleAdded(projectSampleJoin);

		ArgumentCaptor<SampleAddedProjectEvent> captor = ArgumentCaptor.forClass(SampleAddedProjectEvent.class);
		verify(eventRepository).save(captor.capture());

		SampleAddedProjectEvent value = captor.getValue();
		assertEquals(project, value.getProject());
		assertEquals(sample, value.getSample());
	}

	@Test
	public void testProcessUserRemoved() {
		JoinPoint jp = mock(JoinPoint.class);
		User user = new User("tom", null, null, "Some", "guy", null);
		Object[] args = { project, user };
		when(jp.getArgs()).thenReturn(args);

		projectEventAspect.processUserRemoved(jp);

		ArgumentCaptor<UserRemovedProjectEvent> captor = ArgumentCaptor.forClass(UserRemovedProjectEvent.class);
		verify(eventRepository).save(captor.capture());

		UserRemovedProjectEvent value = captor.getValue();
		assertEquals(project, value.getProject());
		assertEquals(user, value.getUser());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessUserRemovedBadArguments() {
		JoinPoint jp = mock(JoinPoint.class);
		User user = new User("tom", null, null, "Some", "guy", null);
		Object[] args = { "Not a project", user };
		when(jp.getArgs()).thenReturn(args);

		projectEventAspect.processUserRemoved(jp);

	}
}
