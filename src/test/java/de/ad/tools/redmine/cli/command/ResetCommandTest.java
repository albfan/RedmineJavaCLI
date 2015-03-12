package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResetCommandTest {
  private Configuration configuration;
  private PrintStream out;

  private ResetCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);

    out = mock(PrintStream.class);

    command = new ResetCommand(configuration, out);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    command.process(arguments);

    verify(configuration).reset();
    verify(out).println(ResetCommand.RESET_SUCCESS_MESSAGE);
  }
}
