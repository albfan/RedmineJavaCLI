package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.PrintStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedmineCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineManager redmineManager;

  private RedmineCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(true);

    out = mock(PrintStream.class);

    redmineManager = mock(RedmineManager.class);

    command = new RedmineCommand("test", "This is a test command.",
        "This is a long description", new Command.Argument[0],
        configuration, out, redmineManager);
  }

  @Test
  public void testCommandSkipsWhenNotConnected() throws Exception {
    String[] arguments = new String[0];

    when(configuration.isConfigured()).thenReturn(false);

    String message = String.format(RedmineCommand.NOT_CONNECTED_MESSAGE,
        command.getName());

    exception.expect(Exception.class);
    exception.expectMessage(message);
    command.process(arguments);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    String message = String.format(RedmineCommand.NOT_CONNECTED_MESSAGE,
        command.getName());

    command.process(arguments);
  }
}
