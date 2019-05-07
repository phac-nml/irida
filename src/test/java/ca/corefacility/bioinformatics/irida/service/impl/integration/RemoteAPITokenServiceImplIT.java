package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
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

	@Before
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
		assertEquals("123456789", token.getTokenString());
	}

	@Test(expected=EntityNotFoundException.class)
	public void testGetTokenNotExists() {
		RemoteAPI api = apiService.read(2L);
		tokenService.getToken(api);
	}
	
	@Test
	public void testAddToken(){
		RemoteAPI api = apiService.read(2L);
		RemoteAPIToken token = new RemoteAPIToken("111111111", api, new Date());
		tokenService.create(token);
		
		RemoteAPIToken readToken = tokenService.getToken(api);
		
		assertEquals(token,readToken);
		
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void testDeleteToken(){
		RemoteAPI api = null;
		try{
			api = apiService.read(1L);
			tokenService.delete(api);
		}catch(EntityNotFoundException ex){
			fail("Token should be able to be deleted");
		}
		
		tokenService.getToken(api);
	}
	
	@Test
	public void addTokenExisting(){
		RemoteAPI api = apiService.read(1L);
		RemoteAPIToken originalToken = tokenService.getToken(api);
		
		RemoteAPIToken token = new RemoteAPIToken("111111111", api, new Date());
		tokenService.create(token);
		
		RemoteAPIToken readToken = tokenService.getToken(api);
		
		assertNotEquals(token,originalToken);
		assertEquals(token,readToken);
		
	}

}
