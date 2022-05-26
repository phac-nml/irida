package ca.corefacility.bioinformatics.irida.service.impl.unit;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectEventRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectEventServiceImpl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectEventServiceImplTest {
	ProjectEventService service;
	Validator validator;
	ProjectEventRepository repository;
	ProjectUserJoinRepository pujRepository;
	UserGroupProjectJoinRepository ugpjRepository;

	@BeforeEach
	public void setUp() {
		repository = mock(ProjectEventRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		ugpjRepository = mock(UserGroupProjectJoinRepository.class);
		service = new ProjectEventServiceImpl(repository, pujRepository, ugpjRepository, validator);
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
		verify(pujRepository).getProjectsForUser(user);
		verify(ugpjRepository).findProjectsByUser(user);
		verify(repository).getEventsForProjects(anyList(), eq(pageable));
	}
}
