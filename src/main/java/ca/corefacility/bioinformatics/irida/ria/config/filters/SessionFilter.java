package ca.corefacility.bioinformatics.irida.ria.config.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter to be used to add constants to the session for templating
 *
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
@Component
public class SessionFilter extends GenericFilterBean {
	private @Value("${session.max.timeout}") int SESSION_TIMEOUT;

	@Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession();

		session.setMaxInactiveInterval(SESSION_TIMEOUT);
		session.setAttribute("SESSION_TIMEOUT", SESSION_TIMEOUT);

		chain.doFilter(request, response);
	}
}
