package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Describes an NCBI SRA Submission's BioSample's files for the UI.
 */
public class NcbiBioSampleModel {
	private final String id;
	private final String bioSample;
	private final String instrumentModel;
	private final String libraryName;
	private final String librarySelection;
	private final String librarySource;
	private final String libraryStrategy;
	private final String libraryConstructionProtocol;
	private final String status;
	private final String accession;
	private final List<SequencingObject> singles;
	private final List<SequencingObject> pairs;

	public NcbiBioSampleModel(NcbiBioSampleFiles bioSample, List<SequencingObject> pairs, List<SequencingObject> singles) {
		this.id = bioSample.getId();
		this.bioSample = bioSample.getBioSample();
		this.instrumentModel = bioSample.getInstrumentModel().getModel();
		this.libraryName = bioSample.getLibraryName();
		this.librarySelection = bioSample.getLibrarySelection().getValue();
		this.librarySource = bioSample.getLibrarySource().getValue();
		this.libraryStrategy = bioSample.getLibraryStrategy().getValue();
		this.libraryConstructionProtocol = bioSample.getLibraryConstructionProtocol();
		this.status = bioSample.getSubmissionStatus().toString();
		this.accession = bioSample.getAccession();
		this.singles = singles;
		this.pairs = pairs;
	}

	public String getId() {
		return id;
	}

	public String getBioSample() {
		return bioSample;
	}

	public String getInstrumentModel() {
		return instrumentModel;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public String getLibrarySelection() {
		return librarySelection;
	}

	public String getLibrarySource() {
		return librarySource;
	}

	public String getLibraryStrategy() {
		return libraryStrategy;
	}

	public String getLibraryConstructionProtocol() {
		return libraryConstructionProtocol;
	}

	public String getStatus() {
		return status;
	}

	public String getAccession() {
		return accession;
	}

	public List<SequencingObject> getSingles() {
		return singles;
	}

	public List<SequencingObject> getPairs() {
		return pairs;
	}
}