package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

public class NCBIBioSample {
	private final String id;
	private final String bioSample;
	private final String instrumentModel;
	private final String libraryName;
	private final String librarySelection;
	private final String librarySource;
	private final String libraryStrategy;
	private final String libraryConstructionProtocol;
	private final String submissionStatus;
	private final String accession;
	private final Set<SingleEndSequenceFile> singles;
	private final Set<SequenceFilePair> pairs;

	public NCBIBioSample(NcbiBioSampleFiles bioSample) {
		this.id = bioSample.getId();
		this.bioSample = bioSample.getBioSample();
		this.instrumentModel = bioSample.getInstrumentModel().getValue();
		this.libraryName = bioSample.getLibraryName();
		this.librarySelection = bioSample.getLibrarySelection().getValue();
		this.librarySource = bioSample.getLibrarySource().getValue();
		this.libraryStrategy = bioSample.getLibraryStrategy().getValue();
		this.libraryConstructionProtocol = bioSample.getLibraryConstructionProtocol();
		this.submissionStatus = bioSample.getSubmissionStatus().toString();
		this.accession = bioSample.getAccession();
		this.singles = bioSample.getFiles();
		this.pairs = bioSample.getPairs();
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

	public String getSubmissionStatus() {
		return submissionStatus;
	}

	public String getAccession() {
		return accession;
	}

	public Set<SingleEndSequenceFile> getSingles() {
		return singles;
	}

	public Set<SequenceFilePair> getPairs() {
		return pairs;
	}
}
