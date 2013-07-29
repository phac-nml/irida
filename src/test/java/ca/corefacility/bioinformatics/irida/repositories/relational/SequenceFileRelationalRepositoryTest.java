/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@ContextConfiguration(locations = {"classpath:/ca/corefacility/bioinformatics/irida/config/testJdbcContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SequenceFileRelationalRepositoryTest {
    
    @Autowired
    private SequenceFileRepository repo;
    
    @Autowired
    private SampleRepository srepo;
    
    @Autowired
    private DataSource dataSource;    

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
     * Test of getFilesForProject method, of class SequenceFileRelationalRepository.
     */
    public void testGetFilesForProject() {

    }

    /**
     * Test of getFilesForSample method, of class SequenceFileRelationalRepository.
     */
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")       
    public void testGetFilesForSample() {
        Sample read = srepo.read(1L);
        
        Collection<SampleSequenceFileJoin> res = repo.getFilesForSample(read);
        assertFalse(res.isEmpty());
        
        for(SampleSequenceFileJoin join : res){
            assertTrue(join.getSubject().equals(read));
            assertNotNull(join.getObject());
            assertNotNull(join.getObject().getFile());
        }
    }

    /**
     * Test of addFileToProject method, of class SequenceFileRelationalRepository.
     */
    public void testAddFileToProject() {

    }

    /**
     * Test of addFileToSample method, of class SequenceFileRelationalRepository.
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
        assertEquals(addFileToSample.getSubject(),sample);
        assertEquals(addFileToSample.getObject(),file);
        
        String qs = "SELECT * FROM sequencefile_sample WHERE sample_id=? AND sequencefile_id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(qs,  sample.getId(),file.getId());
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals(map.get("SAMPLE_ID"),sample.getId());
        assertEquals(map.get("SEQUENCEFILE_ID"),file.getId());
    }

    /**
     * Test of removeFileFromProject method, of class SequenceFileRelationalRepository.
     */
    public void testRemoveFileFromProject() {

    }

    /**
     * Test of removeFileFromSample method, of class SequenceFileRelationalRepository.
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
        List<Map<String, Object>> list = jdbcTemplate.queryForList(qs,  sample.getId(),file.getId());
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}