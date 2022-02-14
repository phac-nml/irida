package ca.corefacility.bioinformatics.irida.config;

import org.springframework.core.env.PropertySource;
import org.springframework.boot.env.PropertiesPropertySourceLoader;

/**
 * Strategy to load '.conf' files into a {@link PropertySource}.
 *
 * Extends PropertiesPropertySourceLoader to support '.conf' files.
 */
public class IridaConfPropertySourceLoader extends PropertiesPropertySourceLoader {

    @Override
	public String[] getFileExtensions() {
		return new String[] { "conf" };
	}
}
