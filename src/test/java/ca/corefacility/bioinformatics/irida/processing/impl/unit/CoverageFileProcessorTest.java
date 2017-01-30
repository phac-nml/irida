package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.any;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.processing.impl.CoverageFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

public class CoverageFileProcessorTest {

	private CoverageFileProcessor processor;
	private SequencingObjectRepository objectRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private ProjectSampleJoinRepository psRepository;
	private QCEntryRepository qcEntryRepository;
	private AnalysisRepository analysisRepository;

	@Before
	public void setup() {
		objectRepository = mock(SequencingObjectRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		psRepository = mock(ProjectSampleJoinRepository.class);
		qcEntryRepository = mock(QCEntryRepository.class);
		analysisRepository = mock(AnalysisRepository.class);

		processor = new CoverageFileProcessor(objectRepository, ssoRepository, psRepository, qcEntryRepository,
				analysisRepository);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGoodCoverage() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		Sample s = new Sample();
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(ssoRepository.getSampleForSequencingObject(o)).thenReturn(new SampleSequencingObjectJoin(s, o));
		when(psRepository.getProjectForSample(s)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s)));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository, times(0)).delete(any(QCEntry.class));
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertTrue("should be positive coverage", qc.isPositive());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBadCoverage() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(5);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		Sample s = new Sample();
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(ssoRepository.getSampleForSequencingObject(o)).thenReturn(new SampleSequencingObjectJoin(s, o));
		when(psRepository.getProjectForSample(s)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s)));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository, times(0)).delete(any(QCEntry.class));
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertFalse("should be bad coverage", qc.isPositive());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveExistingEntry() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		Sample s = new Sample();
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		QCEntry existingQc = new CoverageQCEntry();
		o.setQcEntries(Lists.newArrayList(existingQc));

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(ssoRepository.getSampleForSequencingObject(o)).thenReturn(new SampleSequencingObjectJoin(s, o));
		when(psRepository.getProjectForSample(s)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s)));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository).delete(existingQc);
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertTrue("should be positive coverage", qc.isPositive());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testStopMultipleProjectsWithCoverage() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		Project p2 = new Project();
		p2.setGenomeSize(100L);
		p2.setRequiredCoverage(3);

		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		Sample s = new Sample();

		QCEntry existingQc = new CoverageQCEntry();
		o.setQcEntries(Lists.newArrayList(existingQc));

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(ssoRepository.getSampleForSequencingObject(o)).thenReturn(new SampleSequencingObjectJoin(s, o));
		when(psRepository.getProjectForSample(s))
				.thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s), new ProjectSampleJoin(p2, s)));

		processor.process(fileId);

		verify(qcEntryRepository).delete(existingQc);
		verify(qcEntryRepository, times(0)).save(any(QCEntry.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMultipleProjectsOneWithSettings() {
		Project p = new Project();
		p.setGenomeSize(100L);
		p.setRequiredCoverage(2);
		Project p2 = new Project();
		SequenceFile file = new SequenceFile();
		SequencingObject o = new SingleEndSequenceFile(file);
		Long fileId = 1L;
		Sample s = new Sample();
		AnalysisFastQC fqc = mock(AnalysisFastQC.class);
		Long baseCount = 300L;

		QCEntry existingQc = new CoverageQCEntry();
		o.setQcEntries(Lists.newArrayList(existingQc));

		when(objectRepository.findOne(fileId)).thenReturn(o);
		when(ssoRepository.getSampleForSequencingObject(o)).thenReturn(new SampleSequencingObjectJoin(s, o));
		when(psRepository.getProjectForSample(s)).thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s)));
		when(psRepository.getProjectForSample(s))
				.thenReturn(Lists.newArrayList(new ProjectSampleJoin(p, s), new ProjectSampleJoin(p2, s)));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(file)).thenReturn(fqc);
		when(fqc.getTotalBases()).thenReturn(baseCount);

		processor.process(fileId);

		ArgumentCaptor<CoverageQCEntry> qcCaptor = ArgumentCaptor.forClass(CoverageQCEntry.class);

		verify(qcEntryRepository).delete(existingQc);
		verify(qcEntryRepository).save(qcCaptor.capture());

		CoverageQCEntry qc = qcCaptor.getValue();

		assertEquals("should show 3 times coverage", 3, qc.getCoverage());
		assertTrue("should be positive coverage", qc.isPositive());
	}
}
