package ca.corefacility.bioinformatics.irida.events;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ProjectEventHandlerTest {
	private ProjectEventHandler handler;
	private ProjectEventRepository eventRepository;
	private ProjectSampleJoinRepository psjRepository;
	private ProjectRepository projectRepository;
	private SampleRepository sampleRepository;

	@Before
	public void setup() {
		eventRepository = mock(ProjectEventRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		projectRepository = mock(ProjectRepository.class);
		sampleRepository = mock(SampleRepository.class);
		handler = new ProjectEventHandler(eventRepository, psjRepository, projectRepository, sampleRepository);
	}

	@Test
	public void testDelegateSampleAdded() {
		Class<? extends ProjectEvent> clazz = SampleAddedProjectEvent.class;
		Project project = new Project();
		Sample sample = new Sample();
		ProjectSampleJoin returnValue = new ProjectSampleJoin(project, sample, true);
		Object[] args = { project, sample };
		MethodEvent methodEvent = new MethodEvent(clazz, returnValue, args);

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(new SampleAddedProjectEvent(returnValue));

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof SampleAddedProjectEvent);

		verify(projectRepository).save(any(Project.class));
	}

	@Test
	public void testDelegateUserRole() {
		Class<? extends ProjectEvent> clazz = UserRoleSetProjectEvent.class;
		Project project = new Project();
		User user = new User();
		ProjectUserJoin returnValue = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER);
		Object[] args = { project, user, ProjectRole.PROJECT_USER };
		MethodEvent methodEvent = new MethodEvent(clazz, returnValue, args);

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(new UserRoleSetProjectEvent(returnValue));

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof UserRoleSetProjectEvent);

		verify(projectRepository).save(any(Project.class));
	}

	@Test
	public void testDelegateUserRemoved() {
		Class<? extends ProjectEvent> clazz = UserRemovedProjectEvent.class;
		Project project = new Project();
		User user = new User();
		Object[] args = { project, user };
		MethodEvent methodEvent = new MethodEvent(clazz, null, args);

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(new UserRemovedProjectEvent(project, user));

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof UserRemovedProjectEvent);

		verify(projectRepository).save(any(Project.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleSequenceFileAddedEventSingle() {
		Class<? extends ProjectEvent> clazz = DataAddedToSampleProjectEvent.class;
		Project project = new Project();
		Sample sample = new Sample();
		SequenceFile file = new LocalSequenceFile();
		SingleEndSequenceFile seqObj = new SingleEndSequenceFile(file);
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(sample, seqObj);

		when(psjRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(
				new DataAddedToSampleProjectEvent(project, sample));

		Object[] args = {};
		MethodEvent methodEvent = new MethodEvent(clazz, join, args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof DataAddedToSampleProjectEvent);

		verify(projectRepository).save(any(Project.class));
		verify(sampleRepository).save(any(Sample.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleSequenceFileAddedEventMultipleReturn() {
		Class<? extends ProjectEvent> clazz = DataAddedToSampleProjectEvent.class;
		Project project = new Project();
		Sample sample = new Sample();
		SequenceFile file = new LocalSequenceFile();
		SingleEndSequenceFile seqObj1 = new SingleEndSequenceFile(file);
		SingleEndSequenceFile seqObj2 = new SingleEndSequenceFile(file);
		SampleSequencingObjectJoin join1 = new SampleSequencingObjectJoin(sample, seqObj1);
		SampleSequencingObjectJoin join2 = new SampleSequencingObjectJoin(sample, seqObj2);

		when(psjRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(
				new DataAddedToSampleProjectEvent(project, sample));

		Object[] args = {};
		MethodEvent methodEvent = new MethodEvent(clazz, Lists.newArrayList(join1, join2), args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository).save(captor.capture());
		ProjectEvent event = captor.getValue();
		assertTrue(event instanceof DataAddedToSampleProjectEvent);

		verify(projectRepository).save(any(Project.class));
		verify(sampleRepository).save(any(Sample.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleSequenceFileAddedEventMultipleProjects() {
		Class<? extends ProjectEvent> clazz = DataAddedToSampleProjectEvent.class;
		Project project = new Project("p1");
		Project project2 = new Project("p2");
		Sample sample = new Sample();
		SequenceFile file = new LocalSequenceFile();
		SingleEndSequenceFile seqObj = new SingleEndSequenceFile(file);
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(sample, seqObj);

		when(psjRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true), new ProjectSampleJoin(project2, sample, true)));

		when(eventRepository.save(any(ProjectEvent.class))).thenReturn(
				new DataAddedToSampleProjectEvent(project, sample));

		Object[] args = {};
		MethodEvent methodEvent = new MethodEvent(clazz, join, args);

		handler.delegate(methodEvent);

		ArgumentCaptor<ProjectEvent> captor = ArgumentCaptor.forClass(ProjectEvent.class);
		verify(eventRepository, times(2)).save(captor.capture());
		List<ProjectEvent> allValues = captor.getAllValues();

		Set<Project> projects = Sets.newHashSet(project, project2);
		for (ProjectEvent event : allValues) {
			assertTrue(event instanceof DataAddedToSampleProjectEvent);
			Project eventProject = event.getProject();
			assertTrue(projects.contains(eventProject));
			projects.remove(eventProject);
		}

		verify(projectRepository, times(2)).save(project);
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
