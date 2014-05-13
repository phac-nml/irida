package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit Test for {@link LoginController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class LoginControllerTest {
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		LoginController loginController = new LoginController();
		mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
	}

	@Test
	public void testGetCorrectView() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login/index"));
	}
}
