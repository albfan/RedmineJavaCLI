package de.ad.tools.redmine.cli;

import de.ad.tools.redmine.cli.util.FileUtil;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationTest {

  private Application.ConfigurationManager configurationManager;
  private Application.RedmineCliFactory redmineCliFactory;
  private RedmineCli.RedmineManagerFactory redmineManagerFactory;
  private PrintStream out;

  private RedmineCli redmineCli;

  @Before
  public void setUp() throws Exception {
    configurationManager = mock(
        Application.ConfigurationManager.class);
    redmineCliFactory = mock(
        Application.RedmineCliFactory.class);
    out = mock(PrintStream.class);
    redmineCli = mock(RedmineCli.class);
    redmineManagerFactory = mock(RedmineCli.RedmineManagerFactory.class);

    when(redmineCliFactory.produce(any(Configuration.class),
        any(PrintStream.class), any(RedmineCli.RedmineManagerFactory.class))).
        thenReturn(redmineCli);
  }

  @Test
  public void testInvocation() throws Exception {
    Application application = mock(Application.class);
    Application.instance = application;

    String[] arguments = new String[] { "arg1", "arg2" };

    Application.main(arguments);

    verify(application).run(arguments);
  }

  @Test
  public void testRun() throws Exception {
    Application application = new Application(configurationManager,
        redmineCliFactory, out, redmineManagerFactory);

    String[] arguments = new String[] { "arg1", "arg2" };

    application.run(arguments);

    verify(redmineCli).handleCommand(arguments);
  }

  @Test
  public void testRunWithException() throws Exception {
    Application application = new Application(configurationManager,
        redmineCliFactory, out, redmineManagerFactory);

    String message = "Exception";
    doThrow(new Exception(message)).when(redmineCli)
        .handleCommand(any(String[].class));

    application.run();

    verify(out).println(message);
  }

  public static class ConfigurationManagerTest {

    private FileUtil.FileUtilImpl impl;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
      impl = mock(FileUtil.FileUtilImpl.class);
      FileUtil.impl = impl;
    }

//    @Test
//    public void testLoadConfiguration() throws Exception {
//      Application.ConfigurationManager configurationManager =
//          new Application.ConfigurationManager(
//              Application.CONFIGURATION_FILE_NAME);
//
//      Configuration expected = new Configuration();
//      when(impl.readObjectFromFile(
//          Application.CONFIGURATION_FILE_NAME)).thenReturn(expected);
//
//      Configuration actual = configurationManager.loadConfiguration();
//
//      assertThat(actual).isEqualTo(expected);
//    }

//    @Test
//    public void testLoadConfigurationWithException() throws Exception {
//      Application.ConfigurationManager configurationManager =
//          new Application.ConfigurationManager(
//              Application.CONFIGURATION_FILE_NAME);
//
//      when(impl.exists(Application.CONFIGURATION_FILE_NAME)).thenReturn(
//          true);
//      doThrow(IOException.class).when(impl)
//          .readObjectFromFile(Application.CONFIGURATION_FILE_NAME);
//
//      exception.expect(IllegalStateException.class);
//      configurationManager.loadConfiguration();
//    }

//    @Test
//    public void testPersistConfiguration() throws Exception {
//      Application.ConfigurationManager configurationManager =
//          new Application.ConfigurationManager(
//              Application.CONFIGURATION_FILE_NAME);
//
//      Configuration configuration = new Configuration();
//      configurationManager.persistConfiguration(configuration);
//
//      verify(impl).writeObjectToFile(configuration,
//          Application.CONFIGURATION_FILE_NAME);
//    }

//    @Test
//    public void testPersistConfigurationWithException() throws Exception {
//      Application.ConfigurationManager configurationManager =
//          new Application.ConfigurationManager(
//              Application.CONFIGURATION_FILE_NAME);
//
//      Configuration configuration = new Configuration();
//
//      doThrow(IOException.class).when(impl)
//          .writeObjectToFile(configuration,
//              Application.CONFIGURATION_FILE_NAME);
//
//      configurationManager.persistConfiguration(configuration);
//    }
  }

  public static class RedmineCliFactoryTest {
    @Test
    public void testProduceRedmineCli() throws Exception {
      Application.RedmineCliFactory redmineCliFactory =
          new Application.RedmineCliFactory();
      PrintStream out = mock(PrintStream.class);
      RedmineCli.RedmineManagerFactory redmineManagerFactory =
          mock(RedmineCli.RedmineManagerFactory.class);

      Configuration configuration = mock(Configuration.class);

      RedmineCli expected = new RedmineCli(configuration, out,
          redmineManagerFactory);

      RedmineCli actual = redmineCliFactory.produce(configuration, out,
          redmineManagerFactory);

      assertThat(actual).isEqualTo(expected);
    }
  }
}
