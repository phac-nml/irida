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

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.utils.IdentifiableTestEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@ContextConfiguration(locations = {"classpath:/ca/corefacility/bioinformatics/irida/config/testJdbcContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GenericRelationalRepositoryTest {
    
    @Autowired
    private CRUDRepository<Long, IdentifiableTestEntity> repo;
    
    @Autowired
    private DataSource dataSource;
    
    private IdentifiableRowMapper rowMapper = new IdentifiableRowMapper();
    
    /*@BeforeClass
    public static void before(){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }*/
    
    
    public class IdentifiableRowMapper implements RowMapper<IdentifiableTestEntity>{
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
    public void testCreate() {
        IdentifiableTestEntity p = new IdentifiableTestEntity();
        p.setIntegerValue(5);
        p.setNonNull("not null");
        p.setLabel("a label");
        
        IdentifiableTestEntity created = repo.create(p);
        assertNotNull(created);
        assertNotNull(created.getId());
    }

    /**
     * Test of read method, of class GenericRelationalRepository.
     */
    @Test
    public void testRead() {
        IdentifiableTestEntity read = repo.read(new Long(0));
        assertNotNull(read);
        assertNotNull(read.getId());
        assertEquals(read.getId(),new Long(0));
    }

    /**
     * Test of readMultiple method, of class GenericRelationalRepository.
     
    @Test
    public void testReadMultiple() {
        System.out.println("readMultiple");
        Collection<Identifier> idents = null;
        GenericRelationalRepository instance = new GenericRelationalRepositoryImpl();
        Collection expResult = null;
        Collection result = instance.readMultiple(idents);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of update method, of class GenericRelationalRepository.
     */
    @Test
    public void testUpdate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        try{
            String differentData = "different";
            HashMap<String,Object> changes = new HashMap<>();
            changes.put("nonNull", differentData);
            IdentifiableTestEntity updated = repo.update(new Long(0), changes);
            
            assertNotNull(updated);
            assertEquals(differentData,updated.getNonNull());
            
            List<IdentifiableTestEntity> query = jdbcTemplate.query("SELECT id,nonNull,integerValue,label FROM identifiable WHERE id=0", rowMapper);
            IdentifiableTestEntity entity = query.get(0);
            assertEquals(entity.getNonNull(),differentData);
        }
        catch(IllegalArgumentException|InvalidPropertyException ex){
            fail();
        }
    }

    /**
     * Test of delete method, of class GenericRelationalRepository.
     */
    @Test
    public void testDelete() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        IdentifiableTestEntity p = new IdentifiableTestEntity();
        p.setIntegerValue(5);
        p.setNonNull("not null");
        p.setLabel("a label");
        
        IdentifiableTestEntity created = repo.create(p);
        
        Long id = created.getId();
        repo.delete(id);
        List<IdentifiableTestEntity> query = jdbcTemplate.query("SELECT id,nonNull,integerValue,label FROM identifiable WHERE id=?", rowMapper,id);
        assertTrue(query.isEmpty());

    }

    /**
     * Test of list method, of class GenericRelationalRepository.
     */
    @Test
    public void testList_0args() {
        List<IdentifiableTestEntity> list = repo.list();
        assertFalse(list.isEmpty());
        for(IdentifiableTestEntity entity : list){
            assertNotNull(entity);
            assertNotNull(entity.getId());
        }
    }

    /**
     * Test of list method, of class GenericRelationalRepository.
     */
    @Test
    public void testList_4args() {
        List<IdentifiableTestEntity> list1 = repo.list(0, 2, "nonNull", Order.ASCENDING);
        assertFalse(list1.isEmpty());
        assertEquals(list1.size(),2);
        
        List<IdentifiableTestEntity> list2 = repo.list(0, 5, "nonNull", Order.DESCENDING);
        
        assertNotNull(list2);
        int maxEle = list2.size() - 1;
        assertEquals(list1.get(0).getId(),list2.get(maxEle).getId());   
    }

    /**
     * Test of exists method, of class GenericRelationalRepository.
     */
    @Test
    public void testExists() {
        assertTrue(repo.exists(new Long(0)));
        assertFalse(repo.exists(new Long(5000)));
    }

    /**
     * Test of count method, of class GenericRelationalRepository.
     */
    @Test
    public void testCount() {
        Integer count = repo.count();
        assertNotNull(count);
        assertTrue(count > 2);
    }

}