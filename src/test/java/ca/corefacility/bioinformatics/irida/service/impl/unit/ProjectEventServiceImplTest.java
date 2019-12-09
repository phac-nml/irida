package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

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
		PageRequest pageable = PageRequest.of(0, 1);

		service.getEventsForProject(project, pageable);
		verify(repository).getEventsForProject(project, pageable);
	}

	@Test
	public void testGetEventsForUser() {
		User user = new User();
		PageRequest pageable = PageRequest.of(0, 1);

		service.getEventsForUser(user, pageable);
		verify(repository).getEventsForUser(user, pageable);

	}

}
