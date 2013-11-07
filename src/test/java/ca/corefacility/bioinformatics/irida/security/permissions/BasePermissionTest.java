package ca.corefacility.bioinformatics.irida.security.permissions;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.User;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BasePermission}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class BasePermissionTest {
	private BasePermission<Permittable> basePermission;
	private CrudRepository<Permittable, Long> crudRepository;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		crudRepository = mock(CrudRepository.class);
		basePermission = new PermittablePermission(Permittable.class, crudRepository);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testEntityNotFound() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		when(crudRepository.findOne(1L)).thenReturn(null);
		basePermission.isAllowed(auth, 1L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBizarreExecution() {
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");

		basePermission.isAllowed(auth, new Object());
	}

	private static class PermittablePermission extends BasePermission<Permittable> {

		protected PermittablePermission(Class<Permittable> domainObjectType,
				CrudRepository<Permittable, Long> repository) {
			super(domainObjectType, repository);
		}

		@Override
		public String getPermissionProvided() {
			return "Permittable";
		}

		@Override
		protected boolean customPermissionAllowed(Authentication authentication, Permittable targetDomainObject) {
			return true;
		}

	}

	private static class Permittable {

	}
}
