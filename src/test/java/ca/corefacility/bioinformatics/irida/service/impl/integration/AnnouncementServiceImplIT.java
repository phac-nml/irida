package ca.corefacility.bioinformatics.irida.service.impl.integration;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Integration tests for testing out Announcements
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
        IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
        WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnnouncementServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnnouncementServiceImplIT {

    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private UserService userService;

    @Test
    public void testCreateAnnouncement() {

    }

    @Test
    public void testDeleteAnnouncementSuccess() {

    }

    @Test
    public void testDeleteAnnouncementFailed() {

    }

    @Test
    public void testUserMarkAnnouncementAsReadSuccess() {

    }

    @Test
    public void testUserMarkAnnouncementAsReadFailed() {

    }

    @Test
    public void testUserUnmarkAnnouncementAsReadSuccess() {

    }

    @Test
    public void testUserUnmarkAnnouncementAsReadFailed() {

    }
}
