package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.utils.repositories.IdentifiableTestEntityRepo;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiRepositoriesConfig.class,
		IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class GenericRelationalRepositoryTest {

	@Autowired
	private IdentifiableTestEntityRepo repo;

	@Autowired
	private DataSource dataSource;

	private IdentifiableRowMapper rowMapper = new IdentifiableRowMapper();

	public GenericRelationalRepositoryTest() {
		SecurityUser.setUser();
	}

	public class IdentifiableRowMapper implements RowMapper<IdentifiableTestEntity> {
		@Override
		public IdentifiableTestEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
			IdentifiableTestEntity entity = new IdentifiableTestEntity();
			entity.setId(rs.getLong("id"));
			entity.setNonNull(rs.getString("nonNull"));
			entity.setIntegerValue(rs.getInt("integerValue"));
			entity.setLabel(rs.getString("label"));

			return entity;
		}
	}

	/**
	 * Test of create method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testCreate() {
		IdentifiableTestEntity p = new IdentifiableTestEntity();
		p.setIntegerValue(5);
		p.setNonNull("not null");
		p.setLabel("a label");

		IdentifiableTestEntity created = repo.create(p);
		assertNotNull(created);
		assertNotNull(created.getId());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testCreateNullObject() {
		try {
			repo.create(null);
			fail();
		} catch (IllegalArgumentException ex) {

		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testCreateDuplicate() {
		IdentifiableTestEntity p = new IdentifiableTestEntity();
		p.setIntegerValue(5);
		p.setNonNull("not null");
		p.setLabel("a label");
		p.setId(new Long(1));

		try {
			p = repo.create(p);
			fail();
		} catch (EntityExistsException ex) {

		}

	}

	/**
	 * Test of read method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testRead() {
		IdentifiableTestEntity read = repo.read(new Long(1));
		assertNotNull(read);
		assertNotNull(read.getId());
		assertEquals(read.getId(), new Long(1));
	}

	/**
	 * Test of readMultiple method, of class GenericRelationalRepository.
	 */
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@Test
	public void testReadMultiple() {
		List<Long> ids = new ArrayList<>();
		ids.add(new Long(1));
		ids.add(new Long(2));

		Collection<IdentifiableTestEntity> read = repo.readMultiple(ids);
		assertFalse(read.isEmpty());
		for (IdentifiableTestEntity ent : read) {
			assertNotNull(ent);
			assertNotNull(ent.getId());
			assertNotNull(ent.getNonNull());
		}
	}

	/**
	 * Test of update method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testUpdate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		try {
			String differentData = "different";
			HashMap<String, Object> changes = new HashMap<>();
			changes.put("nonNull", differentData);
			IdentifiableTestEntity updated = repo.update(new Long(1), changes);

			assertNotNull(updated);
			assertEquals(differentData, updated.getNonNull());

			List<IdentifiableTestEntity> query = jdbcTemplate.query(
					"SELECT id,nonNull,integerValue,label FROM identifiable WHERE id=1", rowMapper);
			IdentifiableTestEntity entity = query.get(0);
			assertEquals(entity.getNonNull(), differentData);
		} catch (IllegalArgumentException | InvalidPropertyException ex) {
			fail();
		}
	}

	/**
	 * Test of delete method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testDelete() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Long id = new Long(1);
		repo.delete(id);
		List<IdentifiableTestEntity> query = jdbcTemplate.query(
				"SELECT id,nonNull,integerValue,label FROM identifiable WHERE id=?", rowMapper, id);
		assertTrue(query.isEmpty());
		List<IdentifiableTestEntity> queryAud = jdbcTemplate.query(
				"SELECT id,nonNull,integerValue,label FROM identifiable_AUD WHERE id=?", rowMapper, id);
		assertTrue(queryAud.size() == 1);
		assertEquals(queryAud.get(0).getId(),id);
	}

	/**
	 * Test of list method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testList_0args() {
		List<IdentifiableTestEntity> list = repo.list();
		assertFalse(list.isEmpty());
		for (IdentifiableTestEntity entity : list) {
			assertNotNull(entity);
			assertNotNull(entity.getId());
		}
	}

	/**
	 * Test of list method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testList_4args() {
		List<IdentifiableTestEntity> list1 = repo.list(0, 2, "nonNull", Order.ASCENDING);
		assertFalse(list1.isEmpty());
		assertEquals(list1.size(), 2);

		List<IdentifiableTestEntity> list2 = repo.list(0, 20, "nonNull", Order.DESCENDING);

		assertNotNull(list2);
		int maxEle = list2.size() - 1;
		assertEquals(list1.get(0).getId(), list2.get(maxEle).getId());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testListPaged() {
		List<IdentifiableTestEntity> list1 = repo.list(1, 2, "nonNull", Order.ASCENDING);
		List<IdentifiableTestEntity> list2 = repo.list(2, 1, "nonNull", Order.ASCENDING);
		assertFalse(list1.isEmpty());
		assertFalse(list2.isEmpty());

		assertEquals(list1.get(1), list2.get(0));
	}

	/**
	 * Test of exists method, of class GenericRelationalRepository.
	 */
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@Test
	public void testExists() {
		assertTrue(repo.exists(new Long(0)));
		assertFalse(repo.exists(new Long(5000)));
	}

	/**
	 * Test of count method, of class GenericRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testCount() {
		Integer count = repo.count();
		assertNotNull(count);
		assertTrue(count > 2);
	}
	
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
	public void testCascadeDelete(){
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String query = "SELECT * FROM entityjoin WHERE identifiableTestEntity_id=0";

		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
		assertEquals("Test preconditions not met.  Database has changed", queryForList.size(),2);
		repo.delete(0L);
		
		queryForList = jdbcTemplate.queryForList(query);
		assertTrue(queryForList.isEmpty());
	}
}
