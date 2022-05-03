package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;

import com.google.common.collect.Sets;

/**
 * Tests for {@link RepositoryBackedPermission}.
 * 
 * 
 */
public class RepositoryBackedPermissionTest {
	private RepositoryBackedPermission<Permittable, Long> repositoryBackedPermission;
	private CrudRepository<Permittable, Long> crudRepository;

	private Authentication auth;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		crudRepository = mock(CrudRepository.class);
		repositoryBackedPermission = new PermittablePermission(Permittable.class, Long.class, crudRepository);

		auth = new UsernamePasswordAuthenticationToken("fbristow", "password1");
	}

	/**
	 * Tests successfully allowing permission for a single object by Long id.
	 */
	@Test
	public void testPermissionLongSuccess() {
		when(crudRepository.findById(1L)).thenReturn(Optional.of(new Permittable(1L)));

		assertTrue(repositoryBackedPermission.isAllowed(auth, 1L));
	}

	/**
	 * Tests failing to allow permission due to an id not found.
	 */
	@Test
	public void testEntityNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, 1L);
		});
	}

	/**
	 * Tests successfully allowing permission for collection of single long id.
	 */
	@Test
	public void testPermissionSingleCollectionLongSuccess() {
		when(crudRepository.findById(1L)).thenReturn(Optional.of(new Permittable(1L)));

		assertTrue(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(1L)));
	}

	/**
	 * Tests failing to allow permission for collection of single long id.
	 */
	@Test
	public void testPermissionSingleCollectionLongFail() {
		assertThrows(EntityNotFoundException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(1L));
		});
	}

	/**
	 * Tests successfully allowing permission for collection of two long ids.
	 */
	@Test
	public void testPermissionTwoCollectionLongSuccess() {
		when(crudRepository.findById(1L)).thenReturn(Optional.of(new Permittable(1L)));
		when(crudRepository.findById(2L)).thenReturn(Optional.of(new Permittable(2L)));

		assertTrue(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(1L, 2L)));
	}

	/**
	 * Tests failing to allow permission for collection of two long ids (one id
	 * exists, one doesn't).
	 */
	@Test
	public void testPermissionTwoCollectionLongFail() {
		when(crudRepository.findById(1L)).thenReturn(Optional.of(new Permittable(1L)));

		assertThrows(EntityNotFoundException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(1L, 2L));
		});
	}

	/**
	 * Tests successfully allowing permission for a single object.
	 */
	@Test
	public void testPermissionSuccess() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		assertTrue(repositoryBackedPermission.isAllowed(auth, permittable1));
	}

	/**
	 * Tests denying permission for a single object.
	 */
	@Test
	public void testPermissionFailNotPermitted() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		assertFalse(repositoryBackedPermission.isAllowed(auth, new Permittable(2L)));
	}

	/**
	 * Tests allowing permission for an empty collection.
	 */
	@Test
	public void testPermissionCollectionEmptySuccess() {
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository);

		assertTrue(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet()));
	}

	/**
	 * Tests allowing permission for a collection with a single element.
	 */
	@Test
	public void testPermissionCollectionSuccess() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		assertTrue(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(permittable1)));
	}

	/**
	 * Tests failing permission for a collection with a single element.
	 */
	@Test
	public void testPermissionCollectionFailNotPermitted() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		assertFalse(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(new Permittable(2L))));
	}

	/**
	 * Tests allowing permission for a collection with 2 elements.
	 */
	@Test
	public void testPermissionCollectionSuccessTwoElements() {
		Permittable permittable1 = new Permittable(1L);
		Permittable permittable2 = new Permittable(2L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1,
				permittable2);

		assertTrue(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(permittable1, permittable2)));
	}

	/**
	 * Tests failing permission for a collection with one element allowed and
	 * one element not.
	 */
	@Test
	public void testPermissionCollectionFailTwoElementsOneFail() {
		Permittable permittable1 = new Permittable(1L);
		Permittable permittable2 = new Permittable(2L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		assertFalse(repositoryBackedPermission.isAllowed(auth, Sets.newHashSet(permittable1, permittable2)));
	}

	/**
	 * Tests throwing an exception for an invalid collection type.
	 */
	@Test
	public void testPermissionCollectionFailInvalidType() {
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository);

		assertThrows(IllegalArgumentException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, Sets.newHashSet("invalid"));
		});
	}

	/**
	 * Tests succeeding with non-generic collection type.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPermissionSucccessNonGenericCollectionType() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		Set set = new HashSet();
		set.add(permittable1);

		assertTrue(repositoryBackedPermission.isAllowed(auth, set));
	}

	/**
	 * Tests throwing an exception for a mixed collection type.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPermissionFailMixedCollectionType() {
		Permittable permittable1 = new Permittable(1L);
		repositoryBackedPermission = new VariablePermittablePermission(Permittable.class, Long.class, crudRepository, permittable1);

		Set mixedSet = new HashSet();
		mixedSet.add(permittable1);
		mixedSet.add("invalid");

		assertThrows(IllegalArgumentException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, mixedSet);
		});
	}

	@Test
	public void testBizarreExecution() {
		assertThrows(IllegalArgumentException.class, () -> {
			repositoryBackedPermission.isAllowed(auth, new Object());
		});
	}

	/**
	 * A class which allows the definition of permissions for different objects.
	 * 
	 *
	 */
	private static class VariablePermittablePermission extends RepositoryBackedPermission<Permittable, Long> {

		private Set<Permittable> permittedObjects;

		/**
		 * Builds a new class for checking permissions of different objects.
		 * 
		 * @param domainObjectType
		 *            The type of object to check.
		 * @param repository
		 *            The repository storing this type of object.
		 * @param permittedObjects
		 *            A list of objects which will succeed the permissions
		 *            check.
		 */
		protected VariablePermittablePermission(Class<Permittable> domainObjectType, Class<Long> identifierType,
				CrudRepository<Permittable, Long> repository, Permittable... permittedObjects) {
			super(domainObjectType, identifierType, repository);

			this.permittedObjects = new HashSet<>();
			for (Permittable permittedId : permittedObjects) {
				this.permittedObjects.add(permittedId);
			}
		}

		@Override
		public String getPermissionProvided() {
			return "Permittable";
		}

		@Override
		protected boolean customPermissionAllowed(Authentication authentication, Permittable targetDomainObject) {
			return permittedObjects.contains(targetDomainObject);
		}
	}

	private static class PermittablePermission extends RepositoryBackedPermission<Permittable, Long> {

		protected PermittablePermission(Class<Permittable> domainObjectType, Class<Long> identifierType,
				CrudRepository<Permittable, Long> repository) {
			super(domainObjectType, identifierType, repository);
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

	/**
	 * An object used as a placeholder for checking permissions.
	 * 
	 *
	 */
	private static class Permittable {
		private Long id;

		/**
		 * Builds a new object for checking permissions with a given id.
		 * 
		 * @param id
		 *            The id of the object for checking permissions.
		 */
		public Permittable(Long id) {
			this.id = id;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Permittable other = (Permittable) obj;
			return Objects.equals(id, other.id);
		}
	}
}
