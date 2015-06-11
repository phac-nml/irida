package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.ria.config.BreadCrumbInterceptor;

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
