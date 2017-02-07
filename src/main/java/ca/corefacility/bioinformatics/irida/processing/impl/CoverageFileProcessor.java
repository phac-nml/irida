package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * {@link FileProcessor} used to calculate coverage of a
 * {@link SequencingObject} for a {@link Project}s
 */
@Component
public class CoverageFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(CoverageFileProcessor.class);

	private SequencingObjectRepository objectRepository;

	private SampleSequencingObjectJoinRepository ssoRepository;

	private ProjectSampleJoinRepository psRepository;

	private QCEntryRepository qcEntryRepository;

	private AnalysisRepository analysisRepository;

	@Autowired
	public CoverageFileProcessor(SequencingObjectRepository objectRepository,
			SampleSequencingObjectJoinRepository ssoRepository, ProjectSampleJoinRepository psRepository,
			QCEntryRepository qcEntryRepository, AnalysisRepository analysisRepository) {
		this.objectRepository = objectRepository;
		this.ssoRepository = ssoRepository;
		this.psRepository = psRepository;
		this.qcEntryRepository = qcEntryRepository;
		this.analysisRepository = analysisRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		// read the seqobject, sample and project
		SequencingObject read = objectRepository.findOne(sequenceFileId);

		SampleSequencingObjectJoin sampleJoin = ssoRepository.getSampleForSequencingObject(read);
		List<Join<Project, Sample>> projectForSample = psRepository.getProjectForSample(sampleJoin.getSubject());

		if(read.getQcEntries() != null){
		// remove any existing coverage entries
			read.getQcEntries().stream().filter(q -> q instanceof CoverageQCEntry)
					.forEach(q -> qcEntryRepository.delete(q));
		}

		// find projects with a set genome size and required coverage
		List<Join<Project, Sample>> projectsWithSize = projectForSample.stream()
				.filter(p -> p.getSubject().getGenomeSize() != null && p.getSubject().getRequiredCoverage() != null)
				.collect(Collectors.toList());

		// only run if it's in a single project with coverage settings
		if (projectsWithSize.size() == 1) {
			// get the settings
			Project project = projectsWithSize.iterator().next().getSubject();
			Long projectGenomeSize = project.getGenomeSize();
			Integer requiredCoverage = project.getRequiredCoverage();

			// count the total bases
			long totalBases = read.getFiles().stream().mapToLong(f -> {
				AnalysisFastQC fastqc = analysisRepository.findFastqcAnalysisForSequenceFile(f);
				return fastqc.getTotalBases();
			}).sum();

			// calculate coverage as integer
			int coverage = Math.round(totalBases / projectGenomeSize);

			// check if coverage exceeds the required coverage
			boolean positive = coverage >= requiredCoverage;

			// save the entry
			CoverageQCEntry coverageQCEntry = new CoverageQCEntry(read, coverage, positive);
			qcEntryRepository.save(coverageQCEntry);

		} else {
			logger.debug("Cannot report coverage for object " + read.getId()
					+ ".  It must exist in a single project with a genome size and required coverage.");
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
