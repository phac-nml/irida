package ca.corefacility.bioinformatics.irida.processing.impl;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.babraham.FastQC.Modules.BasicStats;
import uk.ac.babraham.FastQC.Modules.KmerContent;
import uk.ac.babraham.FastQC.Modules.NContent;
import uk.ac.babraham.FastQC.Modules.OverRepresentedSeqs;
import uk.ac.babraham.FastQC.Modules.PerBaseGCContent;
import uk.ac.babraham.FastQC.Modules.PerBaseQualityScores;
import uk.ac.babraham.FastQC.Modules.PerBaseSequenceContent;
import uk.ac.babraham.FastQC.Modules.PerSequenceGCContent;
import uk.ac.babraham.FastQC.Modules.PerSequenceQualityScores;
import uk.ac.babraham.FastQC.Modules.QCModule;
import uk.ac.babraham.FastQC.Modules.SequenceLengthDistribution;
import uk.ac.babraham.FastQC.Sequence.Sequence;
import uk.ac.babraham.FastQC.Sequence.SequenceFactory;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Executes FastQC on a {@link SequenceFile} and stores the report in the
 * database.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class FastqcFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessor.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile process(SequenceFile sequenceFile) throws FileProcessorException {
		Path fileToProcess = sequenceFile.getFile();
		try {
			uk.ac.babraham.FastQC.Sequence.SequenceFile fastQCSequenceFile = SequenceFactory
					.getSequenceFile(fileToProcess.toFile());
			OverRepresentedSeqs os = new OverRepresentedSeqs();
			QCModule[] moduleList = new QCModule[] { new BasicStats(), new PerBaseQualityScores(),
					new PerSequenceQualityScores(), new PerBaseSequenceContent(), new PerBaseGCContent(),
					new PerSequenceGCContent(), new NContent(), new SequenceLengthDistribution(),
					os.duplicationLevelModule(), os, new KmerContent() };
			logger.debug("Launching FastQC analysis modules on all sequences.");
			while (fastQCSequenceFile.hasNext()) {
				Sequence sequence = fastQCSequenceFile.next();
				for (QCModule module : moduleList) {
					module.processSequence(sequence);
				}
			}
			logger.debug("Finished FastQC analysis modules.");
		} catch (Exception e) {
			logger.error("FastQC failed to process the sequence file. Stack trace follows.", e);
			throw new FileProcessorException("FastQC failed to parse the sequence file.");
		}
		return sequenceFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}
}
