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
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;

/**
 * {@link FileProcessor} used to calculate coverage of a
 * {@link SequencingObject} for a {@link Project}s
 */
@Component
public class CoverageFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(CoverageFileProcessor.class);

	private QCEntryRepository qcEntryRepository;

	private AnalysisRepository analysisRepository;

	@Autowired
	public CoverageFileProcessor(QCEntryRepository qcEntryRepository, AnalysisRepository analysisRepository) {
		this.qcEntryRepository = qcEntryRepository;
		this.analysisRepository = analysisRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

	@Override
	public void process(SequencingObject sequencingObject) {
		logger.trace("Counting coverage for file " + sequencingObject);

		if (sequencingObject.getQcEntries() != null) {
			// remove any existing coverage entries
			sequencingObject.getQcEntries().stream().filter(q -> q instanceof CoverageQCEntry)
					.forEach(q -> qcEntryRepository.delete(q));
		}

		// count the total bases
		long totalBases = sequencingObject.getFiles().stream().mapToLong(f -> {
			AnalysisFastQC fastqc = analysisRepository.findFastqcAnalysisForSequenceFile(f);
			return fastqc.getTotalBases();
		}).sum();

		// save the entry
		CoverageQCEntry coverageQCEntry = new CoverageQCEntry(sequencingObject, totalBases);
		qcEntryRepository.save(coverageQCEntry);

	}

}
