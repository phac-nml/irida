package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@DgsComponent
public class ViewerDatafetcher {

	@Autowired
	private UserService userService;

	@DgsData(parentType = "Query", field = "viewer")
	public User viewer(DgsDataFetchingEnvironment dfe) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return userService.getUserByUsername(username);
	}
}
