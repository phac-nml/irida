package ca.corefacility.bioinformatics.irida.processing.impl;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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

		//if we have any existing coverage entries, we will removed them
		if (sequencingObject.getQcEntries() != null) {
			// remove any existing coverage entries
			sequencingObject.getQcEntries().stream().filter(q -> q instanceof CoverageQCEntry)
					.forEach(q -> qcEntryRepository.delete(q));
		}

		try {
			long totalBases = sequencingObject.getFiles().stream().mapToLong(f -> {
				AnalysisFastQC fastqc = analysisRepository.findFastqcAnalysisForSequenceFile(f);

				if (fastqc != null) {
					return fastqc.getTotalBases();
				}
				throw new EntityNotFoundException("No fastqc results for file");
			}).sum();


			// save the entry
			CoverageQCEntry coverageQCEntry = new CoverageQCEntry(sequencingObject, totalBases);
			qcEntryRepository.save(coverageQCEntry);
		} catch (EntityNotFoundException e) {
			logger.warn("Not running coverage as not all files have fastqc results.  Object ID: " + sequencingObject.getId());
		}


	}

}
