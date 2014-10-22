package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;

/**
 * Represents a Sample to be uploaded to Galaxy
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxySample implements UploadSample {
	@Valid
	private UploadFolderName sampleName;
	private List<Path> sampleFiles;

	/**
	 * Builds a new GalaxySample with the given name and list of files.
	 * @param sampleName  The name of the sample.
	 * @param sampleFiles  The list of files belonging to this sample.
	 */
	public GalaxySample(UploadFolderName sampleName, List<Path> sampleFiles) {
		checkNotNull(sampleName, "sampleName is null");
		checkNotNull(sampleFiles, "sampleFiles is null");

		this.sampleName = sampleName;
		this.sampleFiles = sampleFiles;
	}
	
	/**
	 * Builds a new GalaxySample with the given name.
	 * @param sampleName  The name of the sample.
	 */
	public GalaxySample(UploadFolderName sampleName) {
		checkNotNull(sampleName, "sampleName is null");

		this.sampleName = sampleName;
		this.sampleFiles = new LinkedList<>();;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadFolderName getSampleName() {
		return sampleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSampleName(UploadFolderName sampleName) {
		this.sampleName = sampleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Path> getSampleFiles() {
		return sampleFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSampleFiles(List<Path> sampleFiles) {
		checkNotNull(sampleFiles, "sampleFiles are null");
		this.sampleFiles = sampleFiles;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSampleFile(Path sampleFile) {
		checkNotNull(sampleFile, "sampleFile is null");
		if (sampleFiles == null) {
			sampleFiles = new LinkedList<>();
		}
		
		sampleFiles.add(sampleFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(" + sampleName + ", " + sampleFiles + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(sampleName, sampleFiles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxySample other = (GalaxySample) obj;
		
		return Objects.equals(this.sampleName, other.sampleName) &&
				Objects.equals(this.sampleFiles, other.sampleFiles);
	}
}
