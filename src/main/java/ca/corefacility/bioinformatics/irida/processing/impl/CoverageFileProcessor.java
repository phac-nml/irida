package ca.corefacility.bioinformatics.irida.processing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
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

	private QCEntryRepository qcEntryRepository;

	private AnalysisRepository analysisRepository;

	@Autowired
	public CoverageFileProcessor(SequencingObjectRepository objectRepository, QCEntryRepository qcEntryRepository,
			AnalysisRepository analysisRepository) {
		this.objectRepository = objectRepository;
		this.qcEntryRepository = qcEntryRepository;
		this.analysisRepository = analysisRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		logger.trace("Counting coverage for file " + sequenceFileId);

		// read the seqobject
		SequencingObject read = objectRepository.findOne(sequenceFileId);

		if (read.getQcEntries() != null) {
			// remove any existing coverage entries
			read.getQcEntries().stream().filter(q -> q instanceof CoverageQCEntry)
					.forEach(q -> qcEntryRepository.delete(q));
		}

		// count the total bases
		long totalBases = read.getFiles().stream().mapToLong(f -> {
			AnalysisFastQC fastqc = analysisRepository.findFastqcAnalysisForSequenceFile(f);
			return fastqc.getTotalBases();
		}).sum();

		// save the entry
		CoverageQCEntry coverageQCEntry = new CoverageQCEntry(read, totalBases);
		qcEntryRepository.save(coverageQCEntry);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

}
