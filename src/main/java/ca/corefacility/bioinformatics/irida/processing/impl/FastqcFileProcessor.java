package ca.corefacility.bioinformatics.irida.processing.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.babraham.FastQC.FastQCApplication;
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
import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC.AnalysisFastQCBuilder;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * Executes FastQC on a {@link SequenceFile} and stores the report in the
 * database. This is a terrible, ugly, hacky class because most of the internal
 * statistics computed by FastQC are <code>private</code> fields, so we reflect
 * on those fields and make them <code>public</code> to get the values.
 * 
 * 
 */
@Component
public class FastqcFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessor.class);

	private static final String EXECUTION_MANAGER_ANALYSIS_ID = "internal-fastqc";

	private final SequenceFileRepository sequenceFileRepository;
	private final MessageSource messageSource;

	/**
	 * Create a new {@link FastqcFileProcessor}
	 * 
	 * @param messageSource
	 *            the message source for i18n (used to add an internationalized
	 *            description for the analysis).
	 * @param sequenceFileRepository
	 *            the sequence file repository.
	 */
	@Autowired
	public FastqcFileProcessor(final MessageSource messageSource, final SequenceFileRepository sequenceFileRepository) {
		this.messageSource = messageSource;
		this.sequenceFileRepository = sequenceFileRepository;
	}

	@Override
	@Transactional
	public void process(SequencingObject sequencingObject) {
		for (SequenceFile file : sequencingObject.getFiles()) {
			processSingleFile(file);
		}
	}

	/**
	 * Process a single {@link SequenceFile}
	 * 
	 * @param sequenceFile
	 *            file to process
	 * @throws FileProcessorException
	 *             if an error occurs while processing
	 */
	private void processSingleFile(SequenceFile sequenceFile) throws FileProcessorException {
		Path fileToProcess = sequenceFile.getFile();
		AnalysisFastQC.AnalysisFastQCBuilder analysis = AnalysisFastQC.builder()
				.fastqcVersion(FastQCApplication.VERSION).executionManagerAnalysisId(EXECUTION_MANAGER_ANALYSIS_ID)
				.description(messageSource.getMessage("fastqc.file.processor.analysis.description",
						new Object[] { FastQCApplication.VERSION }, LocaleContextHolder.getLocale()));
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
			handleBasicStats(basicStats, analysis);
			handlePerBaseQualityScores(pbqs, analysis);
			handlePerSequenceQualityScores(psqs, analysis);
			handleDuplicationLevel(overRep.duplicationLevelModule(), analysis);
			Set<OverrepresentedSequence> overrepresentedSequences = handleOverRepresentedSequences(overRep);

			logger.trace("Saving FastQC analysis.");
			analysis.overrepresentedSequences(overrepresentedSequences);

			sequenceFile.setFastQCAnalysis(analysis.build());

			sequenceFileRepository.saveMetadata(sequenceFile);
		} catch (Exception e) {
			logger.error("FastQC failed to process the sequence file. Stack trace follows.", e);
			throw new FileProcessorException("FastQC failed to parse the sequence file.", e);
		}
	}

	/**
	 * Handle writing the {@link BasicStats} to the database.
	 * 
	 * @param stats
	 *            the {@link BasicStats} computed by fastqc.
	 * @param analysis
	 *            the {@link AnalysisFastQCBuilder} to update.
	 */
	private void handleBasicStats(BasicStats stats, AnalysisFastQCBuilder analysis) {
		analysis.fileType(stats.getFileType());
		analysis.encoding(PhredEncoding.getFastQEncodingOffset(stats.getLowestChar()).name());
		analysis.minLength(stats.getMinLength());
		analysis.maxLength(stats.getMaxLength());
		analysis.totalSequences((int) stats.getActualCount());
		analysis.filteredSequences((int) stats.getFilteredCount());
		analysis.gcContent(stats.getGCContent());
		analysis.totalBases(
				stats.getACount() + stats.getGCount() + stats.getCCount() + stats.getTCount() + stats.getNCount());
	}

	/**
	 * Handle writing the {@link PerBaseQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerBaseQualityScores} computed by fastqc.
	 * @param analysis
	 *            the {@link AnalysisFastQCBuilder} to update.
	 */
	private void handlePerBaseQualityScores(PerBaseQualityScores scores, AnalysisFastQCBuilder analysis)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		QualityBoxPlot bp = (QualityBoxPlot) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		bp.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		analysis.perBaseQualityScoreChart(image);
	}

	/**
	 * Handle writing the {@link PerSequenceQualityScores} to the database.
	 * 
	 * @param scores
	 *            the {@link PerSequenceQualityScores} computed by fastqc.
	 * @param analysis
	 *            the {@link AnalysisFastQCBuilder} to update.
	 */
	private void handlePerSequenceQualityScores(PerSequenceQualityScores scores, AnalysisFastQCBuilder analysis)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		LineGraph lg = (LineGraph) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		analysis.perSequenceQualityScoreChart(image);
	}

	/**
	 * Handle writing the {@link DuplicationLevel} to the database.
	 * 
	 * @param duplicationLevel
	 *            the {@link DuplicationLevel} calculated by fastqc.
	 * @param analysis
	 *            the {@link AnalysisFastQCBuilder} to update.
	 */
	private void handleDuplicationLevel(DuplicationLevel duplicationLevel, AnalysisFastQCBuilder analysis)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		LineGraph lg = (LineGraph) duplicationLevel.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		ImageIO.write(b, "PNG", os);
		byte[] image = os.toByteArray();
		analysis.duplicationLevelChart(image);
	}

	/**
	 * Handle getting over represented sequences from fastqc.
	 * 
	 * @param seqs
	 *            overrepresented sequences.
	 * @return a collection of {@link OverrepresentedSequence} corresponding to
	 *         the FastQC {@link OverRepresentedSeqs}.
	 */
	private Set<OverrepresentedSequence> handleOverRepresentedSequences(OverRepresentedSeqs seqs) {

		OverrepresentedSeq[] sequences = seqs.getOverrepresentedSequences();
		if (sequences == null) {
			return Collections.emptySet();
		}

		Set<OverrepresentedSequence> overrepresentedSequences = new HashSet<>(sequences.length);

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
