package ca.corefacility.bioinformatics.irida.service.export;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.NcbiExportSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.impl.export.NcbiExportSubmissionServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validator;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link NcbiExportSubmissionService}
 */
public class NcbiExportSubmissionServiceTest {

	NcbiExportSubmissionService service;
	NcbiExportSubmissionRepository repository;
	Validator validator;

	@Before
	public void setup() {
		validator = mock(Validator.class);
		repository = mock(NcbiExportSubmissionRepository.class);

		service = new NcbiExportSubmissionServiceImpl(repository, validator);
	}

	@Test
	public void testCreate() {
		SingleEndSequenceFile sequenceFile = new SingleEndSequenceFile(new SequenceFile());

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles("sample", Sets.newHashSet(sequenceFile),
				Sets.newHashSet(), null, "library_name", null, null, null, "library_construction_protocol",
				"namespace");
		NcbiExportSubmission submission = new NcbiExportSubmission(null, null, "bioProjectId", "organization",
				"ncbiNamespace", new Date(), Lists.newArrayList(ncbiBioSampleFiles));

		service.create(submission);

		verify(repository).save(submission);
	}

	@Test
	public void testCreatePairs() {
		SequenceFile sequenceFile = new SequenceFile();

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles("sample", Sets.newHashSet(),
				Sets.newHashSet(new SequenceFilePair(sequenceFile, sequenceFile)), null, "library_name", null, null,
				null, "library_construction_protocol", "namespace");
		NcbiExportSubmission submission = new NcbiExportSubmission(null, null, "bioProjectId", "organization",
				"ncbiNamespace", new Date(), Lists.newArrayList(ncbiBioSampleFiles));

		service.create(submission);

		verify(repository).save(submission);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNoFiles() {

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles("sample", Sets.newHashSet(), Sets.newHashSet(),
				null, "library_name", null, null, null, "library_construction_protocol", "namespace");
		NcbiExportSubmission submission = new NcbiExportSubmission(null, null, "bioProjectId", "organization",
				"ncbiNamespace", new Date(), Lists.newArrayList(ncbiBioSampleFiles));

		service.create(submission);
	}
}
