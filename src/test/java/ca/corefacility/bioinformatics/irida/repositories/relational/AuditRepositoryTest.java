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

import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevEntity;
import ca.corefacility.bioinformatics.irida.utils.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.utils.IdentifiableTestEntityRepo;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuditRepositoryTest {
    @Autowired
    private IdentifiableTestEntityRepo repo;
    
    @Autowired
    private AuditRepository arepo;
    
    public AuditRepositoryTest() {
    }


    /**
     * Test of getVersion method, of class AuditRepository.
     */
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")    
    @Test
    public void testGetVersion() {
        IdentifiableTestEntity version1 = arepo.getVersion(0L,1, IdentifiableTestEntity.class);
        assertNotNull(version1);
        assertEquals(version1.getNonNull(), "notNull 0");
        
        IdentifiableTestEntity version2 = arepo.getVersion(0L,2, IdentifiableTestEntity.class);
        assertNotNull(version2);
        assertEquals(version2.getNonNull(), "different not null");

    }

    /**
     * Test of getRevisions method, of class AuditRepository.
     */
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")    
    @Test
    public void testGetRevisions() {
        List<UserRevEntity> revisions = arepo.getRevisions(0L, IdentifiableTestEntity.class);
        assertNotNull(revisions);
        assertEquals(revisions.size(), 2);
    }
}