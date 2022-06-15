package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.nio.file.Path;
import java.util.Date;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.ria.web.models.IridaBase;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PairedEndSequenceFileModelTest {
	@Test
	public void testPairedEndSequenceFileModel() {
		long pairId = 1L;
		String pairName = "PAIR NAME";
		Date createdDate = new Date(1623784232L);
		Date modifiedDate = new Date(1655320232L);
		SequenceFilePair pair = mock(SequenceFilePair.class);
		when(pair.getId()).thenReturn(pairId);
		when(pair.getLabel()).thenReturn(pairName);
		when(pair.getCreatedDate()).thenReturn(createdDate);
		when(pair.getModifiedDate()).thenReturn(modifiedDate);

		Path forwardPath = Path.of("src/test/resources/files/pairs/pair_test_1_001.fastq");
		Path reversePath = Path.of("src/test/resources/files/pairs/pair_test_2_001.fastq");
		SequenceFile forwardFile = new SequenceFile(forwardPath);
		SequenceFile reverseFile = new SequenceFile(reversePath);

		when(pair.getForwardSequenceFile()).thenReturn(forwardFile);
		when(pair.getReverseSequenceFile()).thenReturn(reverseFile);

		PairedEndSequenceFileModel pairedEndSequenceFileModel = new PairedEndSequenceFileModel(pair);
		assertThat(pairedEndSequenceFileModel).isInstanceOf(IridaBase.class);

		assertEquals(pairId, pairedEndSequenceFileModel.getId(), "Id should not be changed");
		assertEquals(ModelKeys.PairedEndSequenceFileModel.label + pairId, pairedEndSequenceFileModel.getKey(),
				"Key should be concatenated with id");
		assertEquals(pairName, pairedEndSequenceFileModel.getName(), "Name should not be changed");
		assertEquals(createdDate, pairedEndSequenceFileModel.getCreatedDate(), "Created date should not be changed");
		assertEquals(modifiedDate, pairedEndSequenceFileModel.getModifiedDate(), "Modified date should not be changed");
	}
}