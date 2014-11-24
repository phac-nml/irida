package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectEventServiceImpl;

public class ProjectEventServiceImplTest {
	ProjectEventService service;
	Validator validator;
	ProjectEventRepository repository;

	@Before
	public void setUp() {
		repository = mock(ProjectEventRepository.class);
		service = new ProjectEventServiceImpl(repository, validator);
	}

	@Test
	public void testGetEventsForProject() {
		Project project = new Project("a project");
		PageRequest pageable = new PageRequest(0, 1);

		service.getEventsForProject(project, pageable);
		verify(repository).getEventsForProject(project, pageable);
	}

	@Test
	public void testGetEventsForUser() {
		User user = new User();
		PageRequest pageable = new PageRequest(0, 1);

		service.getEventsForUser(user, pageable);
		verify(repository).getEventsForUser(user, pageable);

	}

	@Test
	public void testGetLastTenEventsForProject() {
		Project project = new Project("a project");

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		service.getLastTenEventsForProject(project);
		verify(repository).getEventsForProject(eq(project), captor.capture());

		assertEquals(10, captor.getValue().getPageSize());
	}

	@Test
	public void testGetLastTenEventsForUser() {
		User user = new User();

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		service.getLastTenEventsForUser(user);
		verify(repository).getEventsForUser(eq(user), captor.capture());

		assertEquals(10, captor.getValue().getPageSize());
	}

}
