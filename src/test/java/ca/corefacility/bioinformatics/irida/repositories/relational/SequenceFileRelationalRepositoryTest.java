package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
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
public class SequenceFileRelationalRepositoryTest {
    
    @Autowired
    private SequenceFileRepository repo;
    
    @Autowired
    private SampleRepository srepo;
    
    @Autowired
    private MiseqRunRepository mrepo;
    
    @Autowired
    private DataSource dataSource;    
    
    public SequenceFileRelationalRepositoryTest(){
        SecurityUser.setUser();
    }

	/**
	 * Test of create method, of class SequenceFileRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreate() {
		SequenceFile file = repo.create(new SequenceFile(Paths.get("/tmp/5")));
		assertNotNull(file);
		assertNotNull(file.getId());
	}

	/**
	 * Test of getFilesForProject method, of class
	 * SequenceFileRelationalRepository.
	 */
	public void testGetFilesForProject() {

	}

	/**
	 * Test of getFilesForSample method, of class
	 * SequenceFileRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetFilesForSample() {
		Sample read = srepo.read(1L);

		Collection<SampleSequenceFileJoin> res = repo.getFilesForSample(read);
		assertFalse(res.isEmpty());
		
		for (SampleSequenceFileJoin join : res) {
			assertTrue(join.getSubject().equals(read));
			assertNotNull(join.getObject());
			assertNotNull(join.getObject().getFile());
		}
	}
    
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")    
    public void testGetFilesForMiseqRun(){
        MiseqRun run = mrepo.findOne(1L);
        
        List<MiseqRunSequenceFileJoin> filesForMiseqRun = repo.getFilesForMiseqRun(run);
        
        assertTrue(filesForMiseqRun.size() == 2);
        for(MiseqRunSequenceFileJoin join : filesForMiseqRun){
            assertEquals(join.getSubject(), run);
            assertNotNull(join.getObject().getFile());
        }
    }


	/**
	 * Test of addFileToSample method, of class
	 * SequenceFileRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testAddFileToSample() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Sample sample = srepo.read(5L);
		SequenceFile file = repo.read(1L);
		SampleSequenceFileJoin addFileToSample = repo.addFileToSample(sample, file);
		assertNotNull(addFileToSample);
		assertEquals(addFileToSample.getSubject(), sample);
		assertEquals(addFileToSample.getObject(), file);

		String qs = "SELECT * FROM sequencefile_sample WHERE sample_id=? AND sequencefile_id=?";
		Map<String, Object> map = jdbcTemplate.queryForMap(qs, sample.getId(), file.getId());
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(map.get("SAMPLE_ID"), sample.getId());
		assertEquals(map.get("SEQUENCEFILE_ID"), file.getId());
	}

	/**
	 * Test of removeFileFromSample method, of class
	 * SequenceFileRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testRemoveFileFromSample() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SequenceFile file = repo.read(1L);
		Sample sample = srepo.read(1L);

		repo.removeFileFromSample(sample, file);

		String qs = "SELECT * FROM sequencefile_sample WHERE sample_id=? AND sequencefile_id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(qs, sample.getId(), file.getId());
		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testAddOverrepresentedSequence() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SequenceFile file = repo.read(1L);
		String sequence = "ACTCA";
		int count = 1;
		BigDecimal percentage = BigDecimal.valueOf(10.01);
		String source = "No Hit";
		OverrepresentedSequence overrepresentedSequence = new OverrepresentedSequence(sequence, count, percentage,
				source);

		repo.addOverrepresentedSequenceToSequenceFile(file, overrepresentedSequence);

		String qs = "select * from sequencefile_overrepresentedsequence where sequencefile_id = ?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(qs, file.getId());
		assertNotNull("No results from query.", list);
		assertEquals("Exactly 1 result should be returned.", 1, list.size());
		Map<String, Object> join = list.iterator().next();
		assertEquals("Wrong sequence_file_id retrieved.", file.getId(), join.get("sequencefile_id"));
		Long overrepresentedSequenceId = Long.valueOf(join.get("overrepresentedsequence_id").toString());
		assertNotNull("No overrepresentedSequenceId in result.", overrepresentedSequenceId);
		qs = "select * from overrepresented_sequence where id = ?";
		list = jdbcTemplate.queryForList(qs, overrepresentedSequenceId);
		assertNotNull("No results from query.", list);
		assertEquals("Only 1 result should be returned.", 1, list.size());
		join = list.iterator().next();
		assertEquals("Wrong sequence stored.", sequence, join.get("sequence"));
		assertEquals("Wrong count stored.", count, join.get("overrepresentedSequenceCount"));
		assertEquals("Wrong percentage stored.", percentage, join.get("percentage"));
		assertEquals("Wrong source stored.", source, join.get("possibleSource"));
	}
}
