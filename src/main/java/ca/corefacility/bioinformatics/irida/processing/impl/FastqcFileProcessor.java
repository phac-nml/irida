package ca.corefacility.bioinformatics.irida.processing.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.babraham.FastQC.Graphs.LineGraph;
import uk.ac.babraham.FastQC.Graphs.QualityBoxPlot;
import uk.ac.babraham.FastQC.Modules.BasicStats;
import uk.ac.babraham.FastQC.Modules.DuplicationLevel;
import uk.ac.babraham.FastQC.Modules.OverRepresentedSeqs;
import uk.ac.babraham.FastQC.Modules.OverRepresentedSeqs.OverrepresentedSeq;
import uk.ac.babraham.FastQC.Modules.PerBaseQualityScores;
import uk.ac.babraham.FastQC.Modules.PerSequenceQualityScores;
import uk.ac.babraham.FastQC.Modules.QCModule;
import uk.ac.babraham.FastQC.Sequence.Sequence;
import uk.ac.babraham.FastQC.Sequence.SequenceFactory;
import uk.ac.babraham.FastQC.Sequence.QualityEncoding.PhredEncoding;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

import com.google.common.collect.ImmutableMap;

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

	public static void main(String[] args) {
		FastqcFileProcessor ffp = new FastqcFileProcessor(null);
		Path file = Paths.get("/home/fbristow/27459_S1_L001_R1_001.fastq.gz");
		SequenceFile sequenceFile = new SequenceFile(file);
		ffp.process(sequenceFile);
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
			OverRepresentedSeqs overRep = new OverRepresentedSeqs();
			QCModule[] moduleList = new QCModule[] { basicStats, pbqs, psqs, overRep };

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
			updatedProperties.putAll(handlePerBaseQualityScores(pbqs));
			updatedProperties.putAll(handlePerSequenceQualityScores(psqs));
			updatedProperties.putAll(handleDuplicationLevel(overRep.duplicationLevelModule()));

			sequenceFile = sequenceFileRepository.update(sequenceFile.getId(), updatedProperties);

			Collection<OverrepresentedSequence> overrepresentedSequences = handleOverRepresentedSequences(overRep);
			for (OverrepresentedSequence sequence : overrepresentedSequences) {
				sequenceFileRepository.addOverrepresentedSequenceToSequenceFile(sequenceFile, sequence);
			}
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
	 * @return properties suitable for updating a {@link SequenceFile}.
	 */
	private Map<String, Object> handleBasicStats(BasicStats stats) {
		Map<String, Object> updatedProperties = new HashMap<>();

		updatedProperties.put("fileType", stats.getFileType());
		updatedProperties.put("encoding", PhredEncoding.getFastQEncodingOffset(stats.getLowestChar()).name());
		updatedProperties.put("minLength", stats.getMinLength());
		updatedProperties.put("maxLength", stats.getMaxLength());
		updatedProperties.put("totalSequences", stats.getActualCount());
		updatedProperties.put("filteredSequences", stats.getFilteredCount());
		updatedProperties.put("gcContent", stats.getGCContent());
		updatedProperties.put("totalBases",
				stats.getACount() + stats.getGCount() + stats.getCCount() + stats.getTCount() + stats.getNCount());

		return updatedProperties;
	}

	/**
	 * Handle writing the {@link PerBaseQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerBaseQualityScores} computed by fastqc.
	 * @return a map with a single key containing a byte array, png-formatted
	 *         chart.
	 */
	private Map<String, Object> handlePerBaseQualityScores(PerBaseQualityScores scores) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		QualityBoxPlot bp = (QualityBoxPlot) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		bp.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		return ImmutableMap.of("perBaseQualityScoreChart", (Object) image);
	}

	/**
	 * Handle writing the {@link PerSequenceQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerSequenceQualityScores} computed by fastqc.
	 * @return a map with a single key containing a byte array, png-formatted
	 *         chart.
	 */
	private Map<String, Object> handlePerSequenceQualityScores(PerSequenceQualityScores scores) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		LineGraph lg = (LineGraph) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		return ImmutableMap.of("perSequenceQualityScoreChart", (Object) image);
	}

	/**
	 * Handle writing the {@link DuplicationLevel} to the database.
	 * 
	 * @param duplicationLevel
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> handleDuplicationLevel(DuplicationLevel duplicationLevel) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		LineGraph lg = (LineGraph) duplicationLevel.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		return ImmutableMap.of("duplicationLevelChart", (Object) image);
	}

	/**
	 * Handle getting over represented sequences from fastqc.
	 * 
	 * @param seqs
	 *            overrepresented sequences.
	 * @return a collection of {@link OverrepresentedSequence} corresponding to
	 *         the FastQC {@link OverRepresentedSeqs}.
	 */
	private Collection<OverrepresentedSequence> handleOverRepresentedSequences(OverRepresentedSeqs seqs) {
		// force FastQC to calculate the over-represented sequences
		// seqs.raisesError();
		OverrepresentedSeq[] sequences = seqs.getOverrepresentedSequences();
		if (sequences == null) {
			return Collections.emptyList();
		}

		Collection<OverrepresentedSequence> overrepresentedSequences = new ArrayList<>(sequences.length);

		for (OverrepresentedSeq s : sequences) {
			String sequenceString = s.seq();
			int count = s.count();
			BigDecimal percent = BigDecimal.valueOf(s.percentage());
			String possibleSource = s.contaminantHit();

			overrepresentedSequences.add(new OverrepresentedSequence(sequenceString, count, percent, possibleSource));
		}
		return overrepresentedSequences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}
}
