package ca.corefacility.bioinformatics.irida.service.analysis.storage;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

/**
 * Updates a sample with the results from a genome assembly.
 */
@Service
public class AssemblySampleUpdatorService implements AnalysisSampleUpdatorService {

	private static final Logger logger = LoggerFactory.getLogger(AssemblySampleUpdatorService.class);

	private final SampleRepository sampleRepository;

	@Autowired
	public AssemblySampleUpdatorService(SampleRepository sampleRepository) {
		this.sampleRepository = sampleRepository;
	}

	@Override
	public void update(Collection<Sample> samples, Analysis analysis) {
		if (samples.size() != 1) {
			throw new RuntimeException("Error: expected only 1 sample, but got " + samples.size() + " samples");
		}

		Sample sample = samples.iterator().next();

		if (sample.getAssembly() == null) {
			sample.setGenomeAssembly(new GenomeAssemblyFromAnalysis((AnalysisAssemblyAnnotation) analysis));
			sampleRepository.save(sample);
		} else {
			logger.debug("Already exists assembly for sample " + sample + ", will not update assembly");
		}
	}
}