package ca.corefacility.bioinformatics.irida.processing.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;

import uk.ac.babraham.FastQC.Modules.BasicStats;
import uk.ac.babraham.FastQC.Modules.PerBaseQualityScores;
import uk.ac.babraham.FastQC.Modules.PerSequenceQualityScores;
import uk.ac.babraham.FastQC.Modules.QCModule;
import uk.ac.babraham.FastQC.Sequence.Sequence;
import uk.ac.babraham.FastQC.Sequence.SequenceFactory;
import uk.ac.babraham.FastQC.Sequence.QualityEncoding.PhredEncoding;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

/**
 * Executes FastQC on a {@link SequenceFile} and stores the report in the
 * database. This is a terrible, ugly, hacky class because most of the internal
 * statistics computed by FastQC are <code>private</code> fields, so we reflect
 * on those fields and make them <code>public</code> to get the values.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class FastqcFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessor.class);

	private SequenceFileRepository sequenceFileRepository;

	public FastqcFileProcessor(SequenceFileRepository sequenceFileRepository) {
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile process(SequenceFile sequenceFile) throws FileProcessorException {
		Path fileToProcess = sequenceFile.getFile();
		try {
			uk.ac.babraham.FastQC.Sequence.SequenceFile fastQCSequenceFile = SequenceFactory
					.getSequenceFile(fileToProcess.toFile());
			BasicStats basicStats = new BasicStats();
			PerBaseQualityScores pbqs = new PerBaseQualityScores();
			PerSequenceQualityScores psqs = new PerSequenceQualityScores();
			QCModule[] moduleList = new QCModule[] { basicStats, pbqs, psqs };
			logger.debug("Launching FastQC analysis modules on all sequences.");
			while (fastQCSequenceFile.hasNext()) {
				Sequence sequence = fastQCSequenceFile.next();
				for (QCModule module : moduleList) {
					module.processSequence(sequence);
				}
			}
			logger.debug("Finished FastQC analysis modules.");
			Map<String, Object> updatedProperties = new HashMap<>();
			updatedProperties.putAll(handleBasicStats(basicStats));
			handlePerBaseQualityScores(pbqs, sequenceFile);
			handlePerSequenceQualityScores(psqs, sequenceFile);

			sequenceFile = sequenceFileRepository.update(sequenceFile.getId(), updatedProperties);
		} catch (Exception e) {
			logger.error("FastQC failed to process the sequence file. Stack trace follows.", e);
			throw new FileProcessorException("FastQC failed to parse the sequence file.");
		}
		return sequenceFile;
	}

	/**
	 * Handle writing the {@link BasicStats} to the database.
	 * 
	 * @param stats
	 *            the {@link BasicStats} computed by fastqc.
	 * @param sequenceFile
	 *            the file uploaded from the sequencer.
	 */
	private Map<String, Object> handleBasicStats(BasicStats stats) {
		DirectFieldAccessor dfa = new DirectFieldAccessor(stats);
		String fileType = (String) dfa.getPropertyValue("fileType");
		Character lowestChar = (Character) dfa.getPropertyValue("lowestChar");
		Integer minLength = (Integer) dfa.getPropertyValue("minLength");
		Integer maxLength = (Integer) dfa.getPropertyValue("maxLength");
		Integer totalSequences = (Integer) dfa.getPropertyValue("actualCount");
		Integer filteredSequences = (Integer) dfa.getPropertyValue("filteredCount");
		String encoding = PhredEncoding.getFastQEncodingOffset(lowestChar).name();

		Long aCount = (Long) dfa.getPropertyValue("aCount");
		Long cCount = (Long) dfa.getPropertyValue("cCount");
		Long gCount = (Long) dfa.getPropertyValue("gCount");
		Long tCount = (Long) dfa.getPropertyValue("tCount");

		Short gcContent = (short) (((gCount + cCount) * 100) / (aCount + cCount + gCount + tCount));

		Map<String, Object> updatedProperties = new HashMap<>();

		updatedProperties.put("fileType", fileType);
		updatedProperties.put("encoding", encoding);
		updatedProperties.put("minLength", minLength);
		updatedProperties.put("maxLength", maxLength);
		updatedProperties.put("totalSequences", totalSequences);
		updatedProperties.put("filteredSequences", filteredSequences);
		updatedProperties.put("gcContent", gcContent);

		return updatedProperties;
	}

	/**
	 * Handle writing the {@link PerBaseQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerBaseQualityScores} computed by fastqc.
	 * @param sequenceFile
	 *            the file uploaded from the sequencer.
	 */
	private void handlePerBaseQualityScores(PerBaseQualityScores scores, SequenceFile sequenceFile) {

	}

	/**
	 * Handle writing the {@link PerSequenceQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerSequenceQualityScores} computed by fastqc.
	 * @param sequenceFile
	 *            the file uploaded from the sequencer.
	 */
	private void handlePerSequenceQualityScores(PerSequenceQualityScores scores, SequenceFile sequenceFile) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}
}
