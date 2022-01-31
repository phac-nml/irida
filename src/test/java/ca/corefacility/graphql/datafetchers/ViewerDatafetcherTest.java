package ca.corefacility.graphql.datafetchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.graphql.datafetchers.ViewerDatafetcher;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@SpringBootTest(classes = { DgsAutoConfiguration.class, ViewerDatafetcher.class })
public class ViewerDatafetcherTest {

	@Autowired
	private DgsQueryExecutor dgsQueryExecutor;

	@MockBean
	private UserService userService;

	@Test
	public void viewer() {
		String username = "jdoe";
		User u = new User();
		u.setId(1L);
		u.setUsername(username);

		Authentication auth = new UsernamePasswordAuthenticationToken(u, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		when(userService.getUserByUsername(username)).thenReturn(u);

		String queriedUsername = dgsQueryExecutor.executeAndExtractJsonPath(" { viewer { id username }}",
				"data.viewer.username");
		assertEquals(username, queriedUsername);
	}
}
