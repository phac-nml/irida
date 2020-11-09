package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.User;

@RestController
@RequestMapping("/ajax/session")
public class SessionAJaxHandler {

	@RequestMapping("")
	public ResponseEntity<SessionInfo> getUserSessionInformation(HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = (User) authentication.getPrincipal();
		/*
		Get the user's preferred locale information
		 */
		Locale locale = Locale.forLanguageTag(user.getLocale());
		return ResponseEntity.ok(new SessionInfo(locale.toLanguageTag(), user.getUsername(), user.getId(),
				user.getSystemRole().getName(), session.getMaxInactiveInterval()));
	}

	class SessionInfo {
		private final String locale;
		private final String username;
		private final Long userid;
		private final String userRole;
		private final int timeout;

		public SessionInfo(String locale, String username, Long userid, String userRole, int timeout) {
			this.locale = locale;
			this.username = username;
			this.userid = userid;
			this.userRole = userRole;
			this.timeout = timeout;
		}

		public String getLocale() {
			return locale;
		}

		public String getUsername() {
			return username;
		}

		public Long getUserid() {
			return userid;
		}

		public String getUserRole() {
			return userRole;
		}

		public int getTimeout() {
			return timeout;
		}
	}
}
