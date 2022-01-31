package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import java.util.ArrayList;
import java.util.List;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Datafetcher to get {@link Project}s associated with a {@link User}.
 */
@DgsComponent
public class UserProjectsDatafetcher {

	@Autowired
	private ProjectService projectService;

	/**
	 * Get a list of {@link Project}s associated with a {@link User}.
	 * 
	 * @param dfe
	 *            the {@link DgsDataFetchingEnvironment}
	 * @return a list of {@link Project}s
	 */
	@DgsData(parentType = "User", field = "projects")
	public List<Project> projects(DgsDataFetchingEnvironment dfe) {
		User user = dfe.getSource();

		List<Join<Project, User>> projectsForUser = projectService.getProjectsForUser(user);
		List<Project> projects = new ArrayList<>();

		for (Join<Project, User> join : projectsForUser) {
			Project project = join.getSubject();
			projects.add(project);
		}

		return projects;
	}
}
