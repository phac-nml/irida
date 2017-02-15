package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry.QCEntryStatus;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.processing.impl.CoverageFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

public class CoverageFileProcessorTest {

	private CoverageFileProcessor processor;
	private SequencingObjectRepository objectRepository;
	private QCEntryRepository qcEntryRepository;
	private AnalysisRepository analysisRepository;

	@Before
	public void setup() {
		objectRepository = mock(SequencingObjectRepository.class);

		qcEntryRepository = mock(QCEntryRepository.class);
		analysisRepository = mock(AnalysisRepository.class);

		processor = new CoverageFileProcessor(objectRepository, qcEntryRepository, analysisRepository);
	}

	@Test
	public void testGoodCoverage() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository, times(0)).delete(any(QCEntry.class));
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();
		qc.addProjectSettings(p);

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertEquals("should be positive coverage", QCEntryStatus.POSITIVE, qc.getStatus());
	}

	@Test
	public void testBadCoverage() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(5);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository, times(0)).delete(any(QCEntry.class));
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();
		qc.addProjectSettings(p);

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertEquals("should be bad coverage", QCEntryStatus.NEGATIVE, qc.getStatus());
	}

	@Test
	public void testRemoveExistingEntry() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		QCEntry existingQc = new CoverageQCEntry();
		o.setQcEntries(Sets.newHashSet(existingQc));

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository).delete(existingQc);
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();
		qc.addProjectSettings(p);

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertEquals("should be positive coverage", QCEntryStatus.POSITIVE, qc.getStatus());
	}

}
