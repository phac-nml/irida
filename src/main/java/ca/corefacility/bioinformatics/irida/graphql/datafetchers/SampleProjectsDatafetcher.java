package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.graphql.dataloaders.ProjectsDataLoader;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Datafetcher to get {@link Project}s associated with a {@link Sample}.
 */
@DgsComponent
public class SampleProjectsDatafetcher {

	@Autowired
	private ProjectService projectService;

	/**
	 * Get a list of {@link Project}s associated with a {@link Sample}.
	 * 
	 * @param dfe
	 *            the {@link DgsDataFetchingEnvironment}
	 * @return a list of {@link Project}s
	 */
	@DgsData(parentType = "Sample", field = "projects")
	public CompletableFuture<List<Project>> projects(DgsDataFetchingEnvironment dfe) {
		Sample sample = dfe.getSource();
		DataLoader<Long, Project> projectsDataLoader = dfe.getDataLoader(ProjectsDataLoader.class);

		List<Long> projectIds = projectService.getProjectIdsForSample(sample);

		return projectsDataLoader.loadMany(projectIds);
	}
}
