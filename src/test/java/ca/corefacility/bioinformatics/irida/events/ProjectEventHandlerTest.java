package ca.corefacility.bioinformatics.irida.events;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;

public class ProjectEventHandlerTest {
	private ProjectEventHandler handler;
	private ProjectEventRepository eventRepository;
	private ProjectSampleJoinRepository psjRepository;

	@Before
	public void setup() {
		eventRepository = mock(ProjectEventRepository.class);
		handler = new ProjectEventHandler(eventRepository, psjRepository);
	}

	@Test
	public void testDelegateSampleAdded() {
		Class<? extends ProjectEvent> clazz = SampleAddedProjectEvent.class;
		Project project = new Project();
		Sample sample = new Sample();
		ProjectSampleJoin returnValue = new ProjectSampleJoin(project, sample);
		Object[] args = { project, sample };
		MethodEvent methodEvent = new MethodEvent(clazz, returnValue, args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof SampleAddedProjectEvent);
	}

	@Test
	public void testDelegateUserRole() {
		Class<? extends ProjectEvent> clazz = UserRoleSetProjectEvent.class;
		Project project = new Project();
		User user = new User();
		ProjectUserJoin returnValue = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER);
		Object[] args = { project, user, ProjectRole.PROJECT_USER };
		MethodEvent methodEvent = new MethodEvent(clazz, returnValue, args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof UserRoleSetProjectEvent);
	}

	@Test
	public void testDelegateUserRemoved() {
		Class<? extends ProjectEvent> clazz = UserRemovedProjectEvent.class;
		Project project = new Project();
		User user = new User();
		Object[] args = { project, user };
		MethodEvent methodEvent = new MethodEvent(clazz, null, args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof UserRemovedProjectEvent);
	}

	@Test
	public void testOtherEvent() {
		Class<? extends ProjectEvent> clazz = ProjectEvent.class;
		Project project = new Project();
		User user = new User();
		Object[] args = { project, user };
		MethodEvent methodEvent = new MethodEvent(clazz, null, args);

		handler.delegate(methodEvent);

		verifyZeroInteractions(eventRepository);
	}
}
