package ca.corefacility.bioinformatics.irida.model.irida;

import java.nio.file.Path;
import java.util.Map;

public interface IridaSequenceFile {

	public Path getFile();

	public Map<String, String> getOptionalProperties();

}
