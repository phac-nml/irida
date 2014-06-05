package ca.corefacility.bioinformatics.irida.ria.webdriver;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.corefacility.bioinformatics.irida.ria.config.WebConfigurer;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Created by josh on 2014-06-05.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebConfigurer.class})
@WebAppConfiguration
public class LoginTest {
	@Autowired
	private WebApplicationContext context;
	private WebClient webClient;

	@Before
	public void setup() {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		webClient = new WebClient();
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc));
	}
}
