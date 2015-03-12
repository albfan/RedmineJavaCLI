package de.ad.tools.redmine.cli;

import java.io.PrintStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationTest {

  private Application.ConfigurationManager configurationManager;
  private Application.RedmineCliFactory redmineCliFactory;
  private PrintStream out;
  private RedmineCli redmineCli;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configurationManager = mock(
        Application.ConfigurationManager.class);
    redmineCliFactory = mock(
        Application.RedmineCliFactory.class);
    out = mock(PrintStream.class);
    redmineCli = mock(RedmineCli.class);

    when(redmineCliFactory.produce(any(Configuration.class),
        any(PrintStream.class))).thenReturn(redmineCli);
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
        redmineCliFactory, out);

    String[] arguments = new String[] { "arg1", "arg2" };

    application.run(arguments);

    verify(redmineCli).handleCommand(arguments);
  }

  @Test
  public void testRunWithException() throws Exception {
    Application application = new Application(configurationManager,
        redmineCliFactory, out);

    String message = "Exception";
    doThrow(new Exception(message)).when(redmineCli)
        .handleCommand(any(String[].class));

    application.run();

    verify(out).println(message);
  }

  @Test
  @Ignore
  public void testProduceRedmineCli() throws Exception {
    Application.RedmineCliFactory redmineCliFactory =
        new Application.RedmineCliFactory();

    Configuration configuration = mock(Configuration.class);

    RedmineCli expected = new RedmineCli(configuration, out);

    RedmineCli actual = redmineCliFactory.produce(configuration, out);

    assertThat(actual).isEqualTo(expected);
  }
}
