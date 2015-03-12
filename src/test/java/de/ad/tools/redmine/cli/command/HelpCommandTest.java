package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HelpCommandTest {
  private Configuration configuration;
  private PrintStream out;

  private HelpCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    out = mock(PrintStream.class);
  }

  @Test
  public void testGeneralHelp() throws Exception {
    String[] arguments = new String[0];

    Map<String, Command> commands = new LinkedHashMap<>();
    commands.put("test1", new TestCommand1(configuration, out));
    commands.put("test2", new TestCommand2(configuration, out));

    command = new HelpCommand(configuration, out, commands);

    command.process(arguments);

    verify(out).println("usage: redmine <command> [<args>]");
    verify(out).println();
    verify(out).println("test1  <mandatory>    This is a test command.        ");
    verify(out).println("test2  [<optional>]   This is another test command.  ");
  }

  @Test
  public void testCommandHelp() throws Exception {
    String[] arguments = new String[]{"test1"};

    Map<String, Command> commands = new LinkedHashMap<>();
    commands.put("test1", new TestCommand1(configuration, out));

    command = new HelpCommand(configuration, out, commands);

    command.process(arguments);

    verify(out).println("command: test1");
    verify(out).println("description: This is a test command.");
//    verify(out).println();
    verify(out).println("usage: redmine test1 <mandatory> ");
//    verify(out).println();
    verify(out).println("<mandatory>  A mandatory argument.  ");
  }

  private static class TestCommand1 extends Command {
    private static final String NAME = "test1";
    private static final String DESCRIPTION =
        "This is a test command.";
    private static final Argument[] ARGUMENTS = new Argument[] {
        new Argument("mandatory", "A mandatory argument.", false) };

    protected TestCommand1(Configuration configuration, PrintStream out) {
      super(NAME, DESCRIPTION, ARGUMENTS, configuration, out);
    }
  }

  private static class TestCommand2 extends Command {
    private static final String NAME = "test2";
    private static final String DESCRIPTION =
        "This is another test command.";
    private static final Argument[] ARGUMENTS = new Argument[] {
        new Argument("optional", "An optional argument.", true) };

    protected TestCommand2(Configuration configuration, PrintStream out) {
      super(NAME, DESCRIPTION, ARGUMENTS, configuration, out);
    }
  }
}
