package ca.corefacility.bioinformatics.irida.graphql.datafetchers;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Datafetcher to get the currently authenticated {@link User}.
 */
@DgsComponent
public class ViewerDatafetcher {

	@Autowired
	private UserService userService;

	/**
	 * Get the currently authenticated {@link User}.
	 * 
	 * @param dfe
	 *            the {@link DgsDataFetchingEnvironment}
	 * @return the {@link User}
	 */
	@DgsData(parentType = "Query", field = "viewer")
	public User viewer(DgsDataFetchingEnvironment dfe) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		return userService.getUserByUsername(username);
	}
}
