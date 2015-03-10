package ca.corefacility.bioinformatics.irida.model.remote;

import java.nio.file.Path;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;

/**
 * {@link SequenceFile} object pulled from a remote IRIDA installation
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = "file")
public class RemoteSequenceFile extends SequenceFile implements RemoteResource {
	private RESTLinks links;
	private RemoteAPI remoteAPI;

	private String fileName;

	/**
	 * {@inheritDoc}
	 */
	public RESTLinks getRestLinks() {
		return links;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLinks(RESTLinks links) {
		this.links = links;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), links);
	}

	/**
	 * Unsupported operation for {@link RemoteSequenceFile}. To get the sequence
	 * file for a {@link RemoteSequenceFile}, call
	 * {@link SequenceFileRemoteRepository#downloadRemoteSequenceFile(RemoteSequenceFile, ca.corefacility.bioinformatics.irida.model.RemoteAPI)}
	 */
	@JsonIgnore
	@Override
	public void setFile(Path file) {
		throw new UnsupportedOperationException(
				"File cannot be set for RemoteSequenceFile.  This class encodes file metadata only.");
	}

	/**
	 * Unsupported operation for {@link RemoteSequenceFile}. To get the sequence
	 * file for a {@link RemoteSequenceFile}, call
	 * {@link SequenceFileRemoteRepository#downloadRemoteSequenceFile(RemoteSequenceFile, ca.corefacility.bioinformatics.irida.model.RemoteAPI)}
	 */
	@JsonIgnore
	@Override
	public Path getFile() {
		throw new UnsupportedOperationException(
				"File cannot be read from RemoteSequenceFile.  This class encodes file metadata only.");
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	@Override
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}

	@Override
	@JsonAnySetter
	public void addOptionalProperty(String key, String value) {
		super.addOptionalProperty(key, value);
	}
}
