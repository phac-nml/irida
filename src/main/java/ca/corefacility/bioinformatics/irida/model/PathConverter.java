package ca.corefacility.bioinformatics.irida.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts a {@link Path} to a {@link String} before persisting a value to a column in a database. Allows us to store a
 * the absolute path to a file instead of trying to store the file in the relational database.
 */
@Converter(autoApply = true)
public class PathConverter implements AttributeConverter<Path, String> {

	@Override
	public String convertToDatabaseColumn(Path attribute) {
		return attribute == null ? null : attribute.toString();
	}

	@Override
	public Path convertToEntityAttribute(String dbData) {
		return dbData == null ? null : Paths.get(dbData);
	}

}
