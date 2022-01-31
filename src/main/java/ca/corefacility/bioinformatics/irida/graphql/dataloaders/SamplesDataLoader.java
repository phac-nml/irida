package ca.corefacility.bioinformatics.irida.graphql.dataloaders;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;
import com.netflix.graphql.dgs.DgsDataLoader;

import org.dataloader.BatchLoader;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * DataLoader for {@link Sample}s.
 */
@DgsDataLoader(name = "samples", caching = true, batching = true, maxBatchSize = 100)
public class SamplesDataLoader implements BatchLoader<Long, Sample> {

	@Autowired
	private Executor graphqlTaskExecutor;

	@Autowired
	SampleService sampleService;

	@Override
	public CompletionStage<List<Sample>> load(List<Long> keys) {
		return CompletableFuture.supplyAsync(() -> {
			return Lists.newArrayList(sampleService.readMultiple(keys));
		}, graphqlTaskExecutor);
	}

}
