package de.ad.tools.redmine.cli;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.PrintStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedmineCliTest {
  private Configuration configuration;
  private PrintStream out;

  private RedmineCli redmineCli;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);
    when(configuration.getServer()).thenReturn("http://test.redmine.com");
    when(configuration.getApiKey()).thenReturn("1234567890");

    out = mock(PrintStream.class);

    redmineCli = new RedmineCli(configuration, out);
  }

  @Test
  public void testInitWhenNotConnected() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(false);

    redmineCli = new RedmineCli(configuration, out);
  }

  @Test
  public void testHandleCommandWithNoArguments() throws Exception {
    String[] arguments = null;

    exception.expect(Exception.class);
    exception.expectMessage(RedmineCli.INVALID_ARGUMENT_MESSAGE);
    redmineCli.handleCommand(arguments);
  }

  @Test
  public void testHandleCommandWithEmptyArguments() throws Exception {
    String[] arguments = new String[0];

    exception.expect(Exception.class);
    exception.expectMessage(RedmineCli.INVALID_ARGUMENT_MESSAGE);
    redmineCli.handleCommand(arguments);
  }

  @Test
  public void testHandleCommandWithInvalidCommand() throws Exception {
    String invalidCommand = "invalid";
    String[] arguments = { invalidCommand };
    String message = String.format(RedmineCli.INVALID_COMMAND_MESSAGE,
        invalidCommand);

    exception.expect(Exception.class);
    exception.expectMessage(message);
    redmineCli.handleCommand(arguments);
  }

  @Test
  public void testHandleCommandWithHelpCommand() throws Exception {
    String command = "help";
    String[] arguments = { command };

    redmineCli.handleCommand(arguments);
  }
}
