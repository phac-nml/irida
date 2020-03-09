package ca.corefacility.bioinformatics.irida.ria.unit.web;

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

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.config.BreadCrumbInterceptor;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link BreadCrumbInterceptor}
 */
public class BreadCrumbInterceptorTest {
	private BreadCrumbInterceptor breadCrumbInterceptor;
	private ProjectService projectService;
	private SampleService sampleService;

	@Before
	public void setUp() {
		this.projectService = mock(ProjectService.class);
		this.sampleService = mock(SampleService.class);
		MessageSource messageSource = mock(MessageSource.class);
		this.breadCrumbInterceptor = new BreadCrumbInterceptor(projectService, sampleService, messageSource);
	}

	@Test
	public void testBreadCrumbs() throws Exception {
		String projectName = "TEST_PROJECT";
		String sampleName = "TEST_SAMPLE";
		when(projectService.read(anyLong())).thenReturn(new Project(projectName));
		when(sampleService.read(anyLong())).thenReturn(new Sample(sampleName));

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/projects/51/samples/4");
		request.addPreferredLocale(Locale.US);

		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, ?> model = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView("test_page", model);
		breadCrumbInterceptor.postHandle(request, response, new Object(), modelAndView);

		ModelMap modelMap = modelAndView.getModelMap();
		assertTrue("Model should have crumbs key", modelMap.containsKey("breadcrumbs"));

		@SuppressWarnings(value = "unchecked") List<Map<String, String>> crumbs = (List<Map<String, String>>) modelMap.get(
				"breadcrumbs");
		assertEquals("Should be 3 links in the crumbs", 3, crumbs.size());
	}

	@Test
	public void testBreadCrumbWithNoIds() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/projects/this/has/no/ids");
		request.addPreferredLocale(Locale.US);

		MockHttpServletResponse response = new MockHttpServletResponse();
		Map<String, ?> model = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView("test_page", model);
		breadCrumbInterceptor.postHandle(request, response, new Object(), modelAndView);

		ModelMap modelMap = modelAndView.getModelMap();
		assertTrue("Model should have crumbs key", modelMap.containsKey("breadcrumbs"));

		@SuppressWarnings(value = "unchecked") List<Map<String, String>> crumbs = (List<Map<String, String>>) modelMap.get(
				"breadcrumbs");
		assertEquals("Should be 1 breadcrumb since there is no id after project", 1, crumbs.size());
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
