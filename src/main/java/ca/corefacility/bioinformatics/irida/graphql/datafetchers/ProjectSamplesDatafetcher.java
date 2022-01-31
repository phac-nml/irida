package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.graphql.dataloaders.SamplesDataLoader;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Datafetcher to get {@link Sample}s associated with a {@link Project}.
 */
@DgsComponent
public class ProjectSamplesDatafetcher {

	@Autowired
	private SampleService sampleService;

	/**
	 * Get a list of {@link Sample}s associated with a {@link Project}.
	 * 
	 * @param dfe
	 *            the {@link DgsDataFetchingEnvironment}
	 * @return a list of {@link Sample}s
	 */
	@DgsData(parentType = "Project", field = "samples")
	public CompletableFuture<List<Sample>> samples(DgsDataFetchingEnvironment dfe) {
		Project project = dfe.getSource();
		DataLoader<Long, Sample> samplesDataLoader = dfe.getDataLoader(SamplesDataLoader.class);

		List<Long> sampleIds = sampleService.getSampleIdsForProject(project);

		return samplesDataLoader.loadMany(sampleIds);
	}
}
