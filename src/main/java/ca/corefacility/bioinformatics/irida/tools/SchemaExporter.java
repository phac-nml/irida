package ca.corefacility.bioinformatics.irida.tools;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
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

	public static void main(String[] args) {
		boolean drop = true;
		boolean create = true;
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

		if (create)
			export(outFile, delimiter, formatter, createSQL);
		if (drop)
			export(outFile, delimiter, formatter, dropSQL);

		context.close();
	}

	private static void export(String outFile, String delimiter, Formatter formatter, String[] createSQL) {
		PrintWriter writer = null;
		try {
			if (!StringUtils.isEmpty(outFile)) {
				writer = new PrintWriter(outFile);
			} else {
				writer = new PrintWriter(System.out);
			}
			for (String string : createSQL) {
				writer.print(formatter.format(string) + "\n" + delimiter + "\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}