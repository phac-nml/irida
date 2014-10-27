package ca.corefacility.bioinformatics.irida.model.remote;

import java.nio.file.Path;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * {@link SequenceFile} object pulled from a remote IRIDA installation
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSequenceFile extends SequenceFile implements RemoteResource {
	private RESTLinks links;
	private String fileName;

	/**
	 * {@inheritDoc}
	 */
	public RESTLinks getLinks() {
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

	@JsonIgnore
	@Override
	public void setFile(Path file) {
		super.setFile(file);
	}

	@JsonIgnore
	@Override
	public Path getFile() {
		return super.getFile();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
