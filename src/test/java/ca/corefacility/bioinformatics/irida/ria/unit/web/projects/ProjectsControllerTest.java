package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import java.util.*;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.util.TreeNode;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link }
 */
public class ProjectsControllerTest {

	public static final String PROJECT_ORGANISM = "E. coli";
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	private static Project project = null;
	// Services
	private ProjectService projectService;
	private ProjectsController controller;
	private ProjectRemoteService projectRemoteService;
	private RemoteAPIService remoteApiService;
	private TaxonomyService taxonomyService;
	private UpdateSamplePermission updateSamplePermission;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		taxonomyService = mock(TaxonomyService.class);
		updateSamplePermission = mock(UpdateSamplePermission.class);
		controller = new ProjectsController(projectService, projectRemoteService,
				taxonomyService, remoteApiService, updateSamplePermission);
		user.setId(1L);
	}

	@Test
	public void testGetSpecificProjectPage() {
		Model model = new ExtendedModelMap();
		assertEquals("Returns the correct Project Page", ProjectsController.SPECIFIC_PROJECT_PAGE,
				controller.getProjectSpecificPage(model));

	}

	@Test
	public void testSearchTaxonomy() {
		String searchTerm = "bac";
		TreeNode<String> root = new TreeNode<>("Bacteria");
		TreeNode<String> child = new TreeNode<>("ChildBacteria");
		child.setParent(root);
		root.addChild(child);
		List<TreeNode<String>> resultList = new ArrayList<>();
		resultList.add(root);

		// the elements that should be at the root
		List<String> results = Lists.newArrayList(searchTerm, "Bacteria");

		when(taxonomyService.search(searchTerm)).thenReturn(resultList);
		List<Map<String, Object>> searchTaxonomy = controller.searchTaxonomy(searchTerm);

		verify(taxonomyService).search(searchTerm);

		assertFalse(searchTaxonomy.isEmpty());
		assertEquals(2, searchTaxonomy.size());

		for (Map<String, Object> element : searchTaxonomy) {
			assertTrue(element.containsKey("text"));
			assertTrue(element.containsKey("id"));
			assertTrue(results.contains(element.get("text")));
		}

	}

	private Project getProject() {
		if (project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setOrganism(PROJECT_ORGANISM);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}

	private List<Join<Project, User>> getUsersForProjectByRole() {
		List<Join<Project, User>> list = new ArrayList<>();
		list.add(new ProjectUserJoin(getProject(), user, ProjectRole.PROJECT_OWNER));
		return list;
	}

	private Page<Project> getProjectUserJoinPage(User user) {
		return new Page<Project>() {
			@Override
			public int getTotalPages() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return 100;
			}

			@Override
			public <U> Page<U> map(Function<? super Project, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 10;
			}

			@Override
			public int getSize() {
				return 10;
			}

			@Override
			public int getNumberOfElements() {
				return 10;
			}

			@Override
			public List<Project> getContent() {
				return TestDataFactory.constructListJoinProjectUser(user);
			}

			@Override
			public boolean hasContent() {
				return true;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return true;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<Project> iterator() {
				return null;
			}
		};
	}

	public Page<Project> getProjectPage() {
		return new Page<Project>() {
			@Override
			public int getTotalPages() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return 100;
			}

			@Override
			public <U> Page<U> map(Function<? super Project, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 10;
			}

			@Override
			public int getSize() {
				return 10;
			}

			@Override
			public int getNumberOfElements() {
				return 100;
			}

			@Override
			public List<Project> getContent() {
				List<Project> list = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					list.add(new Project("project-" + i));
				}
				return list;
			}

			@Override
			public boolean hasContent() {
				return true;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<Project> iterator() {
				return null;
			}
		};
	}
}
