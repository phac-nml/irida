package ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile;

import java.util.Date;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.BaseRecord;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
		when(sequenceFile.getLabel()).thenReturn(fileName);
		when(sequenceFile.getCreatedDate()).thenReturn(createdDate);
		when(sequenceFile.getModifiedDate()).thenReturn(modifiedDate);

		SingleEndSequenceFile singleEndSequenceFile = mock(SingleEndSequenceFile.class);
		when(singleEndSequenceFile.getId()).thenReturn(id);
		when(singleEndSequenceFile.getLabel()).thenReturn(fileName);
		when(singleEndSequenceFile.getCreatedDate()).thenReturn(createdDate);
		when(singleEndSequenceFile.getModifiedDate()).thenReturn(modifiedDate);
		when(singleEndSequenceFile.getSequenceFile()).thenReturn(sequenceFile);

		SingleEndSequenceFileModel model = new SingleEndSequenceFileModel(singleEndSequenceFile);
		assertThat(model).isInstanceOf(BaseRecord.class);

		assertEquals(id, model.getId(), "Id should not be changed");
		assertEquals(ModelKeys.SingleEndSequenceFileModel.label + id, model.getKey(),
				"Key should be concatenated with id");
		assertEquals(fileName, model.getName(), "Name should not be changed");
		assertEquals(createdDate, model.getCreatedDate(), "Created date should not be changed");
		assertEquals(modifiedDate, model.getModifiedDate(), "Modified date should not be changed");
	}
}