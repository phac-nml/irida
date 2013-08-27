package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.AssertionFailure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevEntity;
import ca.corefacility.bioinformatics.irida.utils.IdentifiableTestEntity;
import ca.corefacility.bioinformatics.irida.utils.IdentifiableTestEntityRepo;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

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
    
    @Before
    public void setUp(){
        SecurityUser.setUser();
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
    
    @DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/ident.xml")
    @DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/ident.xml")    
    @Test
    public void testNotLoggedIn(){
        SecurityContextHolder.getContext().setAuthentication(null);
        
        IdentifiableTestEntity ent = new IdentifiableTestEntity();
        ent.setNonNull("notnull");
        ent.setIntegerValue(5);
        ent.setLabel("bleh");
        try{
            repo.create(ent);
            fail();
        }catch(AssertionFailure ex){
        }
    }
    
}