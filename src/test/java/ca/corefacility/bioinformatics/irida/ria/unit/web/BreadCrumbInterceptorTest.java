package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.ria.config.BreadCrumbInterceptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BreadCrumbInterceptor}
 */
public class BreadCrumbInterceptorTest {
	private MessageSource messageSource;
	private BreadCrumbInterceptor breadCrumbInterceptor;

	@Before
	public void setUp() {
		this.messageSource = mock(MessageSource.class);
		this.breadCrumbInterceptor = new BreadCrumbInterceptor(this.messageSource);
	}

	@Test
	public void testBreadCrumbs() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/projects/51/samples/4");
		request.addPreferredLocale(Locale.US);

		when(messageSource.getMessage("bc.projects", null, Locale.US)).thenReturn("Projects");
		when(messageSource.getMessage("bc.samples", null, Locale.US)).thenReturn("Samples");


		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, ?> model = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView("test_page", model);
		breadCrumbInterceptor.postHandle(request, response, new Object(), modelAndView);

		ModelMap modelMap = modelAndView.getModelMap();
		assertTrue("Model should have crumbs key", modelMap.containsKey("crumbs"));

		@SuppressWarnings(value="unchecked")
		List<Map<String, String>> crumbs = (List<Map<String, String>>) modelMap.get("crumbs");
		assertEquals("Should be 4 links in the crumbs", 4, crumbs.size());
	}

	@Test
	public void testBreadCrumbWithNoIds() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/projects/this/has/no/ids");
		request.addPreferredLocale(Locale.US);

		when(messageSource.getMessage("bc.projects", null, Locale.US)).thenReturn("Projects");
		when(messageSource.getMessage("bc.this", null, Locale.US)).thenReturn("This");
		when(messageSource.getMessage("bc.has", null, Locale.US)).thenReturn("Has");
		when(messageSource.getMessage("bc.no", null, Locale.US)).thenReturn("No");
		when(messageSource.getMessage("bc.ids", null, Locale.US)).thenReturn("IDs");

		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, ?> model = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView("test_page", model);
		breadCrumbInterceptor.postHandle(request, response, new Object(), modelAndView);

		ModelMap modelMap = modelAndView.getModelMap();
		assertTrue("Model should have crumbs key", modelMap.containsKey("crumbs"));

		@SuppressWarnings(value="unchecked")
		List<Map<String, String>> crumbs = (List<Map<String, String>>) modelMap.get("crumbs");
		assertEquals("Should be 5 links in the crumbs", 5, crumbs.size());
		final List<String> texts = crumbs.stream()
				.map(x -> x.get("text"))
				.collect(Collectors.toList());
		final List<String> expected = Arrays.asList("Projects", "This", "Has", "No", "IDs");
		assertEquals("Breadcrumbs list of maps should contain the following text values: " + expected,
				texts, expected);
	}

	@Test
	public void testBadBreadCrumbs() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/nothing/51/samples/4");
		request.addPreferredLocale(Locale.US);


		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, ?> model = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView("test_page", model);
		breadCrumbInterceptor.postHandle(request, response, new Object(), modelAndView);

		ModelMap modelMap = modelAndView.getModelMap();
		assertFalse("Model should not have crumbs key", modelMap.containsKey("crumbs"));
	}
}
