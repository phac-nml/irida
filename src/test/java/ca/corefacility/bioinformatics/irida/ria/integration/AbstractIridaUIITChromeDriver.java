package ca.corefacility.bioinformatics.irida.ria.integration;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.concurrent.TimeUnit;

/**
 * Common functionality to all UI integration tests.
 */
@ActiveProfiles("it")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {IridaApiJdbcDataSourceConfig.class,
        IridaApiPropertyPlaceholderConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AbstractIridaUIITChromeDriver {

    public static final int DRIVER_TIMEOUT_IN_SECONDS = 3;

    private static WebDriver driver;

    /**
     * Code to execute *once* before the class.
     */
    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1024, 900));
        driver.manage().timeouts().implicitlyWait(DRIVER_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Code to execute after *each* test.
     */
    @After
    public void tearDown() {
        LoginPage.logout(driver);
    }

    /**
     * Code to execute *once* after the class is finished.
     */
    @AfterClass
    public static void destroy() {
        driver.quit();
    }

    /**
     * Get a reference to the {@link WebDriver} used in the tests.
     * @return the instance of {@link WebDriver} used in the tests.
     */
    public static WebDriver driver() {
        return driver;
    }
}
