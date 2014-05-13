package ca.corefacility.bioinformatics.irida.ria.web;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit test for {@link DashboardController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class DashboardControllerTest {
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		DashboardController dashboardController = new DashboardController();
		mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
	}

	@Test
	public void testGetCorrectView() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("dashboard/index"))
				.andExpect(MockMvcResultMatchers.model().attribute("hello", CoreMatchers.is("Hello IRIDA!")));
	}
}
