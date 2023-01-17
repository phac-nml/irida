package ca.corefacility.bioinformatics.irida.processing.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
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

import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC.AnalysisFastQCBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisOutputFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

import uk.ac.babraham.FastQC.FastQCApplication;
import uk.ac.babraham.FastQC.Graphs.LineGraph;
import uk.ac.babraham.FastQC.Graphs.QualityBoxPlot;
import uk.ac.babraham.FastQC.Modules.*;
import uk.ac.babraham.FastQC.Modules.OverRepresentedSeqs.OverrepresentedSeq;
import uk.ac.babraham.FastQC.Sequence.Sequence;
import uk.ac.babraham.FastQC.Sequence.SequenceFactory;
import uk.ac.babraham.FastQC.Sequence.QualityEncoding.PhredEncoding;

/**
 * Executes FastQC on a {@link SequenceFile} and stores the report in the database. This is a terrible, ugly, hacky
 * class because most of the internal statistics computed by FastQC are <code>private</code> fields, so we reflect on
 * those fields and make them <code>public</code> to get the values.
 */
@Component
public class FastqcFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(FastqcFileProcessor.class);

	private static final String EXECUTION_MANAGER_ANALYSIS_ID = "internal-fastqc";

	private final SequenceFileRepository sequenceFileRepository;
	private final AnalysisOutputFileRepository outputFileRepository;
	private final MessageSource messageSource;

	/**
	 * Create a new {@link FastqcFileProcessor}
	 *
	 * @param messageSource          the message source for i18n (used to add an internationalized description for the
	 *                               analysis).
	 * @param sequenceFileRepository Repository for storing sequence files
	 * @param outputFileRepository   Repository for storing analysis output files
	 */
	@Autowired
	public FastqcFileProcessor(final MessageSource messageSource, final SequenceFileRepository sequenceFileRepository,
			AnalysisOutputFileRepository outputFileRepository) {
		this.messageSource = messageSource;
		this.sequenceFileRepository = sequenceFileRepository;
		this.outputFileRepository = outputFileRepository;
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
	 * @param sequenceFile file to process
	 * @throws FileProcessorException if an error occurs while processing
	 */
	private void processSingleFile(SequenceFile sequenceFile) throws FileProcessorException {
		Path fileToProcess = sequenceFile.getFile();
		AnalysisFastQC.AnalysisFastQCBuilder analysis = AnalysisFastQC.builder()
				.fastqcVersion(FastQCApplication.VERSION)
				.executionManagerAnalysisId(EXECUTION_MANAGER_ANALYSIS_ID)
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

			Path outputDirectory = Files.createTempDirectory("analysis-output");

			handleBasicStats(basicStats, analysis);
			handlePerBaseQualityScores(pbqs, analysis, outputDirectory);
			handlePerSequenceQualityScores(psqs, analysis, outputDirectory);
			handleDuplicationLevel(overRep.duplicationLevelModule(), analysis, outputDirectory);
			Set<OverrepresentedSequence> overrepresentedSequences = handleOverRepresentedSequences(overRep);

			logger.trace("Saving FastQC analysis.");
			analysis.overrepresentedSequences(overrepresentedSequences);

			AnalysisFastQC analysisFastQC = analysis.build();

			sequenceFile.setFastQCAnalysis(analysisFastQC);

			sequenceFileRepository.saveMetadata(sequenceFile);
		} catch (Exception e) {
			logger.error("FastQC failed to process the sequence file: " + e.getMessage());
			throw new FileProcessorException("FastQC failed to parse the sequence file.", e);
		}
	}

	/**
	 * Handle writing the {@link BasicStats} to the database.
	 *
	 * @param stats    the {@link BasicStats} computed by fastqc.
	 * @param analysis the {@link AnalysisFastQCBuilder} to update.
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
	 * @param scores        the {@link PerBaseQualityScores} computed by fastqc.
	 * @param analysis      the {@link AnalysisFastQCBuilder} to update.
	 * @param tempDirectory the {@link Path} to the temp directory to write the files to.
	 */
	private void handlePerBaseQualityScores(PerBaseQualityScores scores, AnalysisFastQCBuilder analysis,
			Path tempDirectory) throws IOException {
		QualityBoxPlot bp = (QualityBoxPlot) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		bp.paint(g, b.getWidth(), b.getHeight());

		AnalysisOutputFile file = writeImageToFile(tempDirectory, b, "perBaseQualityScoreChart.png");
		analysis.perBaseQualityScoreChart(file);
	}

	/**
	 * Handle writing the {@link PerSequenceQualityScores} to the database.
	 *
	 * @param scores        the {@link PerSequenceQualityScores} computed by fastqc.
	 * @param analysis      the {@link AnalysisFastQCBuilder} to update.
	 * @param tempDirectory the {@link Path} to the temp directory to write the files to.
	 */
	private void handlePerSequenceQualityScores(PerSequenceQualityScores scores, AnalysisFastQCBuilder analysis,
			Path tempDirectory) throws IOException {
		LineGraph lg = (LineGraph) scores.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		AnalysisOutputFile file = writeImageToFile(tempDirectory, b, "perSequenceQualityScoreChart.png");
		analysis.perSequenceQualityScoreChart(file);
	}

	/**
	 * Handle writing the {@link DuplicationLevel} to the database.
	 *
	 * @param duplicationLevel the {@link DuplicationLevel} calculated by fastqc.
	 * @param analysis         the {@link AnalysisFastQCBuilder} to update.
	 * @param tempDirectory    the {@link Path} to the temp directory to write the files to.
	 */
	private void handleDuplicationLevel(DuplicationLevel duplicationLevel, AnalysisFastQCBuilder analysis,
			Path tempDirectory) throws IOException {
		LineGraph lg = (LineGraph) duplicationLevel.getResultsPanel();
		BufferedImage b = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.getGraphics();
		lg.paint(g, b.getWidth(), b.getHeight());

		AnalysisOutputFile file = writeImageToFile(tempDirectory, b, "duplicationLevelChart.png");
		analysis.duplicationLevelChart(file);
	}

	/**
	 * Handle getting over represented sequences from fastqc.
	 *
	 * @param seqs overrepresented sequences.
	 * @return a collection of {@link OverrepresentedSequence} corresponding to the FastQC {@link OverRepresentedSeqs}.
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

	private AnalysisOutputFile writeImageToFile(Path tempDirectory, BufferedImage imageBuffer, String fileName)
			throws IOException {
		Path filePath = tempDirectory.resolve(fileName);

		ImageIO.write(imageBuffer, "PNG", filePath.toFile());

		AnalysisOutputFile analysisOutputFile = outputFileRepository
				.save(new AnalysisOutputFile(filePath, null, fileName, null));

		return analysisOutputFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

	@Override
	public boolean shouldProcessFile(SequencingObject sequencingObject) {
		// we don't want to run the processor for zipped or unknown fast5 files.
		// It will just fail and create a qc entry even though it did what it
		// was supposed to
		if (sequencingObject instanceof Fast5Object) {
			Fast5Object fast5 = (Fast5Object) sequencingObject;
			if (!fast5.getFast5Type().equals(Fast5Object.Fast5Type.SINGLE)) {
				return false;
			}
		}

		return true;
	}
}
