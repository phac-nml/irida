package ca.corefacility.bioinformatics.irida.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;

/**
 * A schema exporter class that will generate DDL for classes annotated with
 * Hibernate annotations. This class is a modified version of the class
 * published at <a href=
 * "http://doingenterprise.blogspot.ca/2012/05/schema-generation-with-hibernate-4-jpa.html"
 * >Enterprise Software Development Blog</a>
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class SchemaExporter {
	private static final Logger logger = LoggerFactory.getLogger(SchemaExporter.class);

	public static void main(String[] args) {
		boolean drop = false;
		boolean create = false;
		String outFile = null;
		String delimiter = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				if (args[i].equals("--drop")) {
					drop = true;
				} else if (args[i].equals("--create")) {
					create = true;
				} else if (args[i].startsWith("--output=")) {
					outFile = args[i].substring(9);
				} else if (args[i].startsWith("--delimiter=")) {
					delimiter = args[i].substring(12);
				}
			}
		}

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("prod");

		context.register(IridaApiPropertyPlaceholderConfig.class);
		context.register(IridaApiJdbcDataSourceConfig.class);
		context.refresh();

		Configuration hibernateConfiguration = context.getBean(Configuration.class);
		Formatter formatter = FormatStyle.DDL.getFormatter();

		String[] createSQL = hibernateConfiguration.generateSchemaCreationScript(Dialect
				.getDialect(hibernateConfiguration.getProperties()));
		String[] dropSQL = hibernateConfiguration.generateDropSchemaScript(Dialect.getDialect(hibernateConfiguration
				.getProperties()));

		if (drop) {
			logger.debug("Dumping SQL 'drop' statements.");
			export(outFile, delimiter, formatter, dropSQL, StandardOpenOption.TRUNCATE_EXISTING);
		}
		if (create) {
			OpenOption openOption;
			if (drop) {
				// if both drop and create are being used, we definitely want to
				// append to the file
				openOption = StandardOpenOption.APPEND;
			} else {
				openOption = StandardOpenOption.TRUNCATE_EXISTING;
			}
			logger.debug("Dumping SQL 'create' statements.");
			export(outFile, delimiter, formatter, createSQL, openOption);
		}

		context.close();
	}

	private static void export(String outFile, String delimiter, Formatter formatter, String[] createSQL,
			OpenOption... options) {

		if (!StringUtils.isEmpty(outFile)) {
			try {
				Files.write(Paths.get(outFile), (StringUtils.join(createSQL, delimiter + "\n") + "\n").getBytes(),
						options);
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			for (String string : createSQL) {
				System.out.print(formatter.format(string) + "\n" + delimiter + "\n");
			}
		}

	}
}