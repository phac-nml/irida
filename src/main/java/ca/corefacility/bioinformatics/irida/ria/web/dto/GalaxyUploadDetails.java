package ca.corefacility.bioinformatics.irida.ria.web.dto;

import java.nio.file.Path;

public class GalaxyUploadDetails {
	private String datasetLibraryId;
	private Path path;
	private String dirToRemove;
	private Path fileToRemove;

	public GalaxyUploadDetails(String datasetLibraryId, Path path, String dirToRemove, Path fileToRemove) {
		this.datasetLibraryId = datasetLibraryId;
		this.path = path;
		this.dirToRemove = dirToRemove;
		this.fileToRemove = fileToRemove;
	}

	public String getDatasetLibraryId() {
		return datasetLibraryId;
	}

	public void setDatasetLibraryId(String datasetLibraryId) {
		this.datasetLibraryId = datasetLibraryId;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Path getFileToRemove() {
		return fileToRemove;
	}

	public void setFileToRemove(Path fileToRemove) {
		this.fileToRemove = fileToRemove;
	}

	public String getDirToRemove() {
		return dirToRemove;
	}

	public void setDirToRemove(String dirToRemove) {
		this.dirToRemove = dirToRemove;
	}
}
