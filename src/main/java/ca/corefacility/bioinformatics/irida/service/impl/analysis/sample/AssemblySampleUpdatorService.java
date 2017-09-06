package ca.corefacility.bioinformatics.irida.service.impl.analysis.sample;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.sample.AnalysisSampleUpdatorService;

/**
 * Updates a sample with the results from a genome assembly.
 */
@Service
public class AssemblySampleUpdatorService implements AnalysisSampleUpdatorService {

	private static final Logger logger = LoggerFactory.getLogger(AssemblySampleUpdatorService.class);

	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	@Autowired
	public AssemblySampleUpdatorService(SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository) {
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
	}

	@Override
	@PreAuthorize("hasPermission(#samples, 'canUpdateSample')")
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) {
		if (samples.size() != 1) {
			throw new RuntimeException("Error: expected only 1 sample, but got " + samples.size() + " samples");
		}

		Sample sample = samples.iterator().next();
		SampleGenomeAssemblyJoin sampleGenomeAssemblyJoin = new SampleGenomeAssemblyJoin(sample,
				new GenomeAssemblyFromAnalysis(analysis));
		
		logger.trace("Saving join for sample [" + sample.getId() + "] to analysis submission [" + analysis.getId() + "]");
		sampleGenomeAssemblyJoinRepository.save(sampleGenomeAssemblyJoin);
	}
}