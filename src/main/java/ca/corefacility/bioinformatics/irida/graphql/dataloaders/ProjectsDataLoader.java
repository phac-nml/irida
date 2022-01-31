package ca.corefacility.bioinformatics.irida.graphql.dataloaders;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import com.netflix.graphql.dgs.DgsDataLoader;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * DataLoader for {@link Project}s.
 */
@DgsDataLoader(name = "projects", caching = true, batching = true, maxBatchSize = 100)
public class ProjectsDataLoader implements BatchLoader<Long, Project> {

	@Autowired
	private Executor graphqlTaskExecutor;

	@Autowired
	ProjectService projectService;

	@Override
	public CompletionStage<List<Project>> load(List<Long> keys) {
		return CompletableFuture.supplyAsync(() -> {
			return Lists.newArrayList(projectService.readMultiple(keys));
		}, graphqlTaskExecutor);
	}
}
