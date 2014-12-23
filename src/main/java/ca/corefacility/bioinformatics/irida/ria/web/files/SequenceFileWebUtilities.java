package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;

/**
 * Utilities class for converting {@link SequenceFile} objects for the web
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Component
public class SequenceFileWebUtilities {
	// Converters
	Formatter<Date> dateFormatter;
	Converter<Long, String> fileSizeConverter;

	public SequenceFileWebUtilities() {
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	/**
	 * Get a Map representation of a {@link SequenceFile}
	 * 
	 * @param file
	 *            The sequence file to convert
	 * @return The sequence file map
	 * @throws IOException
	 *             if reading the file fails
	 */
	public Map<String, Object> getFileDataMap(SequenceFile file) throws IOException {
		Path path = file.getFile();
		Long realSize = 0L;

		if (Files.exists(path)) {
			realSize = Files.size(path);
		}
		String size = fileSizeConverter.convert(realSize);
		Map<String, Object> m = new HashMap<>();
		m.put("id", file.getId().toString());
		m.put("label", file.getLabel());
		m.put("realCreatedDate", file.getCreatedDate());
		m.put("createdDate", dateFormatter.print(file.getCreatedDate(), LocaleContextHolder.getLocale()));
		m.put("size", size);
		m.put("realSize", realSize.toString());
		return m;
	}
}
