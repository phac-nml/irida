package ca.corefacility.bioinformatics.irida.ria.web;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit Test for {@link PageController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PageControllerTest {
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		PageController pageController = new PageController();
		mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
	}

	@Test
	public void testLoginModelAndView() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("pages/login/index"));
	}

	@Test
	public void testMainModelAndView() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("pages/dashboard/index"))
				.andExpect(MockMvcResultMatchers.model().attribute("hello", CoreMatchers.is("Hello IRIDA!")));
	}
}
