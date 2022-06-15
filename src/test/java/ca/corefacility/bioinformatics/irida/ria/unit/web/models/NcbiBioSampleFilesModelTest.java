package ca.corefacility.bioinformatics.irida.ria.unit.web.models;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.*;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiBioSampleFilesModel;

import com.google.common.collect.ImmutableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NcbiBioSampleFilesModelTest {
	@Test
	public void testNcbiBioSamplesFilesModel() {
		String id = "1";
		String namespace = "BIOSAMPLE_NAME";
		String bioSample = "BIOSAMPLE";
		NcbiInstrumentModel instrumentModel = NcbiInstrumentModel.ABSOLID;
		NcbiLibrarySelection librarySelection = NcbiLibrarySelection.CFH;
		String libraryName = "LIBRARY_NAME";
		NcbiLibrarySource librarySource = NcbiLibrarySource.GENOMIC;
		NcbiLibraryStrategy libraryStrategy = NcbiLibraryStrategy.AMPLICON;
		String libraryConstructionProtocol = "LIBRARY_CONSTRUCTION_PROTOCOL";
		String accession = "BIOSAMPLE_ACCESSION";

		SequenceFile file1 = new SequenceFile(
				Path.of("src/test/resources/files/sequence-files/01-1111_S1_L001_R1_001.fastq"));
		Set<SingleEndSequenceFile> singles = ImmutableSet.of(new SingleEndSequenceFile(file1));
		Set<SequenceFilePair> pairs = ImmutableSet.of();

		NcbiBioSampleFiles bioSampleFiles = new NcbiBioSampleFiles(bioSample, singles, pairs, instrumentModel,
				libraryName, librarySelection, librarySource, libraryStrategy, libraryConstructionProtocol, namespace);
		bioSampleFiles.setId(id);
		bioSampleFiles.setAccession(accession);

		NcbiBioSampleFilesModel model = new NcbiBioSampleFilesModel(bioSampleFiles);

		assertEquals(id, model.getId(), "Id should not be changed");
		assertEquals(bioSample, model.getBioSample(), "Biosample should not be changed");
		assertEquals(instrumentModel.getValue(), model.getInstrumentModel(), "Instrument model should not be changed");
		assertEquals(libraryName, model.getLibraryName(), "Library name should not be changed");
		assertEquals(librarySelection.getValue(), model.getLibrarySelection(),
				"Library selection should not be changed");
		assertEquals(librarySource.getValue(), model.getLibrarySource(), "Library source should not be changed");
		assertEquals(libraryStrategy.getValue(), model.getLibraryStrategy(), "Library strategy should not be changed");
		assertEquals(libraryConstructionProtocol, model.getLibraryConstructionProtocol(),
				"Library construction protocol should not be changed");
		assertEquals(ExportUploadState.NEW.toString(), model.getStatus(), "Status should be set properly");
		assertEquals(pairs.size(), model.getPairs().size(), "Pairs should not be changed");
		assertEquals(singles.size(), model.getSingles().size(), "Singles should not be changed");
	}
}