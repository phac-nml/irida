package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.nio.file.Path;
import java.util.List;

import javax.validation.Valid;

import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;

/**
 * Represents a Sample to be uploaded to Galaxy
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxySample implements UploadSample {
	@Valid
	private UploadObjectName sampleName;
	private List<Path> sampleFiles;

	public GalaxySample(UploadObjectName sampleName, List<Path> sampleFiles) {
		checkNotNull(sampleName, "sampleName is null");
		checkNotNull(sampleFiles, "sampleFiles is null");

		this.sampleName = sampleName;
		this.sampleFiles = sampleFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.corefacility.bioinformatics.irida.model.upload.galaxy.UploadSample
	 * #getSampleName()
	 */
	@Override
	public UploadObjectName getSampleName() {
		return sampleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.corefacility.bioinformatics.irida.model.upload.galaxy.UploadSample
	 * #setSampleName
	 * (ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName
	 * )
	 */
	@Override
	public void setSampleName(UploadObjectName sampleName) {
		this.sampleName = sampleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.corefacility.bioinformatics.irida.model.upload.galaxy.UploadSample
	 * #getSampleFiles()
	 */
	@Override
	public List<Path> getSampleFiles() {
		return sampleFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.corefacility.bioinformatics.irida.model.upload.galaxy.UploadSample
	 * #setSampleFiles(java.util.List)
	 */
	@Override
	public void setSampleFiles(List<Path> sampleFiles) {
		checkNotNull(sampleFiles, "sampleFiles are null");
		this.sampleFiles = sampleFiles;
	}

	@Override
	public String toString() {
		return "(" + sampleName + ", " + sampleFiles + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sampleFiles == null) ? 0 : sampleFiles.hashCode());
		result = prime * result
				+ ((sampleName == null) ? 0 : sampleName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxySample other = (GalaxySample) obj;
		if (sampleFiles == null) {
			if (other.sampleFiles != null)
				return false;
		} else if (!sampleFiles.equals(other.sampleFiles))
			return false;
		if (sampleName == null) {
			if (other.sampleName != null)
				return false;
		} else if (!sampleName.equals(other.sampleName))
			return false;
		return true;
	}
}
