package ca.corefacility.bioinformatics.irida.service.impl.analysis.sample;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.sample.AnalysisSampleUpdatorService;

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
	@PreAuthorize("hasPermission(#samples, 'canUpdateSample')")
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) {
		if (samples.size() != 1) {
			throw new RuntimeException("Error: expected only 1 sample, but got " + samples.size() + " samples");
		}

		Sample sample = samples.iterator().next();

		if (!sample.hasGenomeAssemblies()) {
			sample.setGenomeAssemblies(Lists.newArrayList(new GenomeAssemblyFromAnalysis(analysis)));
			sampleRepository.save(sample);
		} else {
			List<GenomeAssembly> assembliesList = sample.getGenomeAssemblies();
			assembliesList.add(new GenomeAssemblyFromAnalysis(analysis));
			sample.setGenomeAssemblies(assembliesList);
			sampleRepository.save(sample);
		}
	}
}