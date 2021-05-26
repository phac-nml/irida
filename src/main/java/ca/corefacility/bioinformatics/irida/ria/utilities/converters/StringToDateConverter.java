package ca.corefacility.bioinformatics.irida.ria.utilities.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * Converts an ISO8601-format String to java.util.Date
 *
 */
@Component
public class StringToDateConverter implements Converter<String, Date> {
    /**
     * Converts a file length property (bytes) to kilobytes.
     *
     * @param source ISO8601-formatted date (YYYY-MM-DD)
     * @return Date object
     */
    @Override
    public Date convert(String source) {
        return DatatypeConverter.parseDate(source).getTime();
    }
}
