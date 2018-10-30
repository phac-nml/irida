package ca.corefacility.bioinformatics.irida.pipeline.results.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;

/**
 * Updates a sample with the results from a genome assembly.
 */
@Component
public class AssemblySampleUpdater implements AnalysisSampleUpdater {

	private static final Logger logger = LoggerFactory.getLogger(AssemblySampleUpdater.class);

	private final GenomeAssemblyRepository genomeAssemblyRepository;
	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	@Autowired
	public AssemblySampleUpdater(GenomeAssemblyRepository genomeAssemblyRepository,
			SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository) {
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
		this.genomeAssemblyRepository = genomeAssemblyRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) {
		checkArgument(samples.size() == 1, "Error: expected only 1 sample, but got " + samples.size() + " samples");

		Sample sample = samples.iterator().next();

		GenomeAssembly genomeAssembly = new GenomeAssemblyFromAnalysis(analysis);
		SampleGenomeAssemblyJoin sampleGenomeAssemblyJoin = new SampleGenomeAssemblyJoin(sample, genomeAssembly);

		logger.trace(
				"Saving join for sample [" + sample.getId() + "] to analysis submission [" + analysis.getId() + "]");
		sampleGenomeAssemblyJoinRepository.save(sampleGenomeAssemblyJoin);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisType getAnalysisType() {
		return AnalysisType.ASSEMBLY_ANNOTATION;
	}
}