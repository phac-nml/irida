package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SingleEndSequenceFileModelTest {
	@Test
	public void testSingleEndSequenceFileModel() {
		Long id = 1L;
		String fileName = "fileName";
		Date createdDate = new Date(1590160849L);
		Date modifiedDate = new Date(1653232849L);

		SequenceFile sequenceFile = mock(SequenceFile.class);
		when(sequenceFile.getId()).thenReturn(id);
	}
}