package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Datafetcher for {@link Project}s.
 */
@DgsComponent
public class ProjectsDatafetcher {

	@Autowired
	private ProjectService projectService;

	/**
	 * Create a new {@link Project}
	 * 
	 * @param projectInput
	 *            the {@link ProjectInput}
	 * @return the created {@link Project}
	 */
	@DgsData(parentType = "Mutation", field = "createProject")
	public Project createProject(@InputArgument("input") ProjectInput projectInput) {
		return projectService.create(projectInput.toProject());
	}
}

class ProjectInput {
	private String name;
	private String projectDescription;
	private String organism;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public Project toProject() {
		Project project = new Project(name);

		if (projectDescription != null) {
			project.setProjectDescription(projectDescription);
		}
		if (organism != null) {
			project.setOrganism(organism);
		}

		return project;
	}
}
