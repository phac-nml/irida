package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.corefacility.bioinformatics.irida.model.user.User;

import com.google.common.base.Strings;
import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

/**
 * This interceptor configures the users gravatar image url, only
 * one time per session.
 */
@Component
public class GravatarInterceptor extends HandlerInterceptorAdapter {
	private static final String GRAVATAR_ATTRIBUTE = "gravatar";

	@Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
		HttpSession session = request.getSession();
		String gravatarAttr = (String) session.getAttribute(GRAVATAR_ATTRIBUTE);
		if (isAuthenticated() && Strings.isNullOrEmpty(gravatarAttr)) {
			User user = (User) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			Gravatar gravatar = new Gravatar(30, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.MONSTERID);
			String gravatarUrl = gravatar.getUrl(user.getEmail());
			session.setAttribute(GRAVATAR_ATTRIBUTE, gravatarUrl);
		}
	}

	private boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;
	}
}
