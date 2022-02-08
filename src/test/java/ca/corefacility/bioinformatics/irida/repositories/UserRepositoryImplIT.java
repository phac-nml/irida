package ca.corefacility.bioinformatics.irida.repositories;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@Tag("IntegrationTest") @Tag("Service")
@SpringBootTest
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/UserServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserRepositoryImplIT {

	@Autowired
	private UserRepository userDetailsService;

	@Test
	public void testLoadUserByUsername() {
		userDetailsService.loadUserByUsername("fbristow");
	}

	@Test
	public void testFailToLoadUserByUsername() {
		assertThrows(UsernameNotFoundException.class, () -> {
			userDetailsService.loadUserByUsername("this is really terrible.");
		});
	}
	
	@Test
	public void testLoadUserByEmail() {
		userDetailsService.loadUserByEmail("manager@nowhere.com");
	}

	@Test
	public void testFailToLoadUserByEmail() {
		assertThrows(EntityNotFoundException.class, () -> {
			userDetailsService.loadUserByEmail("this is really terrible.");
		});
	}
}
