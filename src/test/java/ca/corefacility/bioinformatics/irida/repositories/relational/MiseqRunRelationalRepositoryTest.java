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

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiRepositoriesConfig.class,
		IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class MiseqRunRelationalRepositoryTest {
    
    @Autowired
    private MiseqRunRepository repo;
    
    @Autowired
    private SequenceFileRepository seqrepo;
    
    public MiseqRunRelationalRepositoryTest() {
        SecurityUser.setUser();
    }

    /**
     * Test of addSequenceFileToMiseqRun method, of class MiseqRunRelationalRepository.
     */
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")    
    public void testAddSequenceFileToMiseqRun() {
        SequenceFile seqfile = seqrepo.read(5L);
        MiseqRun run = repo.read(3L);
        
        MiseqRunSequenceFileJoin addSequenceFileToMiseqRun = null;
        try{
            addSequenceFileToMiseqRun = repo.addSequenceFileToMiseqRun(run, seqfile);
        }
        catch(EntityExistsException ex){
            fail();
        }
        
        assertNotNull(addSequenceFileToMiseqRun);
        assertEquals(addSequenceFileToMiseqRun.getSubject(), run);
        assertEquals(addSequenceFileToMiseqRun.getObject(), seqfile);
    }
    
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")    
    public void testAddSequenceFileToMultipleMiseqRun() {
        SequenceFile seqfile = seqrepo.read(1L);
        MiseqRun run = repo.read(3L);
        
        try{
            MiseqRunSequenceFileJoin addSequenceFileToMiseqRun = repo.addSequenceFileToMiseqRun(run, seqfile);
            fail();
        }
        catch(EntityExistsException ex){
            
        }
    }
    
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
    public void testGetMiseqRunForSequenceFile(){
        SequenceFile file = seqrepo.read(1L);
        MiseqRunSequenceFileJoin miseqRun = null;
        try{
             miseqRun = repo.getMiseqRunForSequenceFile(file);
        }
        catch(EntityNotFoundException | StorageException ex){
            fail(ex.getMessage());
        }
        
        assertNotNull(miseqRun);
        assertEquals(miseqRun.getObject().getId(), file.getId());
        assertTrue(miseqRun.getSubject().getId() == 1L);
    }
    
    @Test
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
    public void testGetInvalidMiseqRunForSequenceFile(){
        SequenceFile file = seqrepo.read(5L);
        MiseqRunSequenceFileJoin miseqRun = null;
        try{
             miseqRun = repo.getMiseqRunForSequenceFile(file);
             fail();
        }
        catch(EntityNotFoundException ex){
        }

    }

}