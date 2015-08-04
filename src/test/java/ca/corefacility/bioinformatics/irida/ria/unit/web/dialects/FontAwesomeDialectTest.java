package ca.corefacility.bioinformatics.irida.ria.unit.web.dialects;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import ca.corefacility.bioinformatics.irida.ria.dialects.FontAwesomeDialect;

/**
 * Unit test for the {@link FontAwesomeDialect}
 */
public class FontAwesomeDialectTest {
	private static final IContext EMPTY_CONTEXT = new Context();
	private static final String SAVE_ICON = "<span class=\"fa fa-save\"></span>";
	private static final String DELETE_ICON = "<span class=\"fa fa-trash-o\"></span>";
	private static final String ID_ICON_FIXED_WIDTH = "<span class=\"fa fa-barcode fa-fw\"></span>";
	private static final String TERMINAL_ICON_2X = "<span class=\"fa fa-terminal fa-2x\"></span>";
	private static final String ICON_WITH_ATTRIBUTES = "<span class=\"fa fa-chevron-right\" ng-click=\"ctrl.show()\"></span>";


	@Test
	public void testIridaDialect() throws Exception {
		final TemplateEngine engine = initTemplateEngine();
		final Writer writer = new StringWriter();

		engine.process("files/dialects/icons/good-icons.html", EMPTY_CONTEXT, writer);
		String page = writer.toString();
		assertTrue("Should create a span tag with save icon", page.contains(SAVE_ICON));
		assertTrue("Should create a span tag with delete icon", page.contains(DELETE_ICON));
		assertTrue("Should create a span tag with id icon with class for fixed width 'fa-fw'", page.contains(ID_ICON_FIXED_WIDTH));
		assertTrue("Should create a span tag with terminal icon with class for twice the size 'fa-2x'", page.contains(TERMINAL_ICON_2X));
		assertTrue("Should allow for extra attributes on the element.", page.contains(ICON_WITH_ATTRIBUTES));
	}

	@Test(expected = TemplateProcessingException.class)
	public void testIconNotDeclared() throws Exception{
		final TemplateEngine engine = initTemplateEngine();
		final Writer writer = new StringWriter();

		engine.process("files/dialects/icons/bad-icons.html", EMPTY_CONTEXT, writer);
	}

	private static TemplateEngine initTemplateEngine() throws Exception {
		final TemplateEngine engine = new TemplateEngine();

		final FontAwesomeDialect fontAwesomeDialect = new FontAwesomeDialect();
		engine.addDialect(fontAwesomeDialect);

		final ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setTemplateMode("HTML5");
		engine.setTemplateResolver(classLoaderTemplateResolver);

		return engine;
	}
}
