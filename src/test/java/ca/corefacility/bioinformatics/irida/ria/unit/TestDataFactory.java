package ca.corefacility.bioinformatics.irida.ria.unit;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Generates test data for unit tests.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class TestDataFactory {
	public static final int NUM_PROJECT_SAMPLES = 12;
	public static final int NUM_PROJECT_USERS = 50;
	public static final long NUM_TOTAL_ELEMENTS = 100L;
	public static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	public static final String PROJECT_NAME = "test_project";
	public static final Long PROJECT_ID = 1L;
	private static Project project = null;

	/**
	 * Creates a Page of Projects for testing.
	 * 
	 * @return Page of Projects (1 project)
	 */
	public static Page<ProjectUserJoin> getProjectsPage() {
		return new Page<ProjectUserJoin>() {
			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return NUM_TOTAL_ELEMENTS;
			}

			@Override
			public boolean hasPreviousPage() {
				return false;
			}

			@Override
			public boolean isFirstPage() {
				return false;
			}

			@Override
			public boolean hasNextPage() {
				return false;
			}

			@Override
			public boolean isLastPage() {
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
			public Iterator iterator() {
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			public List getContent() {
				ArrayList<Object> list = new ArrayList<>();
				list.add(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER));
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
		};
	}

	/**
	 * Mocks a List of Project and Samples
	 * 
	 * @return A list of {@link ProjectSampleJoin}
	 */
	public static List<Join<Project, Sample>> getSamplesForProject() {
		List<Join<Project, Sample>> list = new ArrayList<>();
		for (int i = 0; i < NUM_PROJECT_SAMPLES; i++) {
			list.add(new ProjectSampleJoin(project, new Sample("sample_" + i)));
		}
		return list;
	}

	/**
	 * Mocks a List of Project and Users
	 * 
	 * @return A list of {@link ProjectUserJoin}
	 */
	public static List<Join<Project, User>> getUsersForProject() {
		List<Join<Project, User>> list = new ArrayList<>();
		for (int i = 0; i < NUM_PROJECT_USERS; i++) {
			list.add(new ProjectUserJoin(project, new User(), ProjectRole.PROJECT_USER));
		}
		return list;
	}

	public static User getUser() {
		return user;
	}

	/**
	 * Accessor for a static project.
	 * 
	 * @return Static project
	 */
	public static Project getProject() {
		if(project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setModifiedDate(new Date());
		}
		return project;
	}
}
