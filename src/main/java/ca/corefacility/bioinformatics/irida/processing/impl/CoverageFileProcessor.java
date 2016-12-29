package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

@Component
public class CoverageFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(CoverageFileProcessor.class);

	@Autowired
	private SequencingObjectRepository objectRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private ProjectSampleJoinRepository psRepository;

	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequencingObject read = objectRepository.findOne(sequenceFileId);

		SampleSequencingObjectJoin sampleJoin = ssoRepository.getSampleForSequencingObject(read);
		List<Join<Project, Sample>> projectForSample = psRepository.getProjectForSample(sampleJoin.getSubject());

		if (projectForSample.size() == 1) {

			Project project = projectForSample.iterator().next().getSubject();
			if (project.getGenomeSize() == null) {
				Long projectGenomeSize = project.getGenomeSize();

				long totalBases = read.getFiles().stream().mapToLong(f -> f.getFastQCAnalysis().getTotalBases()).sum();

				Long coverage = totalBases / projectGenomeSize;
			} else {
				logger.debug("Cannot report coverage for object " + read.getId()
						+ " as it's project has no reference length");
			}
		} else {
			logger.debug("Cannot report coverage for object " + read.getId() + " as it's in multiple projects.");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

}
