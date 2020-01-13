package ca.corefacility.bioinformatics.irida.ria.utilities.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

@Component
public class StringToDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        return DatatypeConverter.parseDate(source).getTime();
    }
}
