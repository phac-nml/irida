package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/RemoteAPITokenServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RemoteAPITokenServiceImplIT {
	@Autowired
	UserService userService;
	@Autowired
	RemoteAPITokenService tokenService;
	@Autowired
	RemoteAPIService apiService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setUp() {
		User u = new User();
		u.setUsername("tom");
		u.setPassword(passwordEncoder.encode("Password1!"));
		u.setSystemRole(Role.ROLE_USER);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1!",
				ImmutableList.of(Role.ROLE_USER));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void testGetToken() {
		RemoteAPI api = apiService.read(1L);
		RemoteAPIToken token = tokenService.getToken(api);
		assertNotNull(token);
		assertEquals(token.getTokenString(), "123456789");
	}

	@Test
	public void testGetTokenNotExists() {
		RemoteAPI api = apiService.read(2L);
		assertThrows(EntityNotFoundException.class, () -> {
			tokenService.getToken(api);
		});
	}

	@Test
	public void testAddToken() {
		RemoteAPI api = apiService.read(2L);
		RemoteAPIToken token = new RemoteAPIToken("111111111", api, new Date());
		tokenService.create(token);

		RemoteAPIToken readToken = tokenService.getToken(api);

		assertEquals(token, readToken);

	}

	@Test
	public void testDeleteToken() {
		assertThrows(EntityNotFoundException.class, () -> {
			RemoteAPI api = null;
			try {
				api = apiService.read(1L);
				tokenService.delete(api);
			} catch (EntityNotFoundException ex) {
				fail("Token should be able to be deleted");
			}

			tokenService.getToken(api);
		});
	}

	@Test
	public void addTokenExisting() {
		RemoteAPI api = apiService.read(1L);
		RemoteAPIToken originalToken = tokenService.getToken(api);

		RemoteAPIToken token = new RemoteAPIToken("111111111", api, new Date());
		tokenService.create(token);

		RemoteAPIToken readToken = tokenService.getToken(api);

		assertNotEquals(token, originalToken);
		assertEquals(token, readToken);

	}

}
