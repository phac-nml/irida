package ca.corefacility.bioinformatics.irida.ria.unit.web.dialects;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import ca.corefacility.bioinformatics.irida.ria.dialects.IridaDialect;

/**
 * Created by josh on 15-05-22.
 */
public class IridaDialectTest {
	private static final Logger logger = LoggerFactory.getLogger(IridaDialectTest.class);
	private static final IContext EMPTY_CONTEXT = new Context();


	@Test
	public void testIridaDialect() throws Exception {
		final TemplateEngine engine = initTemplateEngine();
		final Writer writer = new StringWriter();

		engine.process("files/irida-dialect-test.html", EMPTY_CONTEXT, writer);
		String page = writer.toString();
		assertEquals("Should create a span tag", "span");
	}

	private static TemplateEngine initTemplateEngine() throws Exception {
		final TemplateEngine engine = new TemplateEngine();

		final IridaDialect iridaDialect = new IridaDialect();
		engine.addDialect(iridaDialect);

		final ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setTemplateMode("HTML5");
		engine.setTemplateResolver(classLoaderTemplateResolver);

		return engine;
	}
}
