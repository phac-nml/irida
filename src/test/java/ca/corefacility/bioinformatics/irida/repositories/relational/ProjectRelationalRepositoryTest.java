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
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@ContextConfiguration(locations = {"classpath:/ca/corefacility/bioinformatics/irida/config/testJdbcContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectRelationalRepositoryTest {
    
    @Autowired
    private ProjectRepository repo;
    
    @Autowired
    private UserRepository urepo;
    
    @Autowired
    private DataSource dataSource;
    
    @Test
    public void testAddUserToProject(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Project project = repo.create(new Project("a new project"));
        User user = new User("anon", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("ROLE_USER"));
        user = urepo.create(user);
        
        
        ProjectUserJoin addUserToProject = repo.addUserToProject(project, user);
        assertNotNull(addUserToProject);
        assertEquals(addUserToProject.getSubject(),project);
        assertEquals(addUserToProject.getObject(),user);
        
        String qs = "SELECT * FROM project_user WHERE project_id=? AND user_id=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(qs,  project.getId(),user.getId());
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertEquals(map.get("PROJECT_ID"),project.getId());
        assertEquals(map.get("USER_ID"),user.getId());
    }
    
    @Test
    public void testRemoveUserFromProject(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Project project = repo.create(new Project("a new project"));
        User user = new User("anon5", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
        user.setRole(new Role("ROLE_USER"));
        user = urepo.create(user);
        
        ProjectUserJoin addUserToProject = repo.addUserToProject(project, user);
        assertNotNull(addUserToProject);
        
        repo.removeUserFromProject(project, user);
        
        String qs = "SELECT * FROM project_user WHERE project_id=? AND user_id=?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(qs,  project.getId(),user.getId());
        assertNotNull(list);
        assertTrue(list.isEmpty());

    }    
    
}
