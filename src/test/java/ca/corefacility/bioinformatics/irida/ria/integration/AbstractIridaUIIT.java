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
import org.openqa.selenium.phantomjs.PhantomJSDriver;
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
public class AbstractIridaUIIT {

    private static final int DRIVER_TIMEOUT_IN_SECONDS = 3;

    private final WebDriver driver;

    private static AbstractIridaUIIT instance;

    protected AbstractIridaUIIT() {
        this.driver = driverToUse();
    }

    /**
     * Code to execute *once* before the class. This method uses {@link PhantomJSDriver} by default. If you want to override that behaviour, override the method {@link AbstractIridaUIIT#driver}.
     */
    @BeforeClass
    public static void setUp() {
        instance = new AbstractIridaUIIT();
        instance.driver.manage().window().setSize(new Dimension(1024, 900));
        instance.driver.manage().timeouts().implicitlyWait(DRIVER_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
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
        instance.driver.quit();
    }

    /**
     * The {@link WebDriver} to use for the tests. Uses {@link PhantomJSDriver} by default. Override if you want to use a different driverToUse.
     * @return an instance of a {@link WebDriver} to use for the tests.
     */
    public WebDriver driverToUse() {
        return new PhantomJSDriver();
    }

    /**
     * Get a reference to the {@link WebDriver} used in the tests.
     * @return the instance of {@link WebDriver} used in the tests.
     */
    public WebDriver driver() {
        return driver;
    }
}
