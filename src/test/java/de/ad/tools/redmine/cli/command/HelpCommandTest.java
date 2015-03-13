package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HelpCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;

  private HelpCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);
  }

  @Test
  public void testGeneralHelp() throws Exception {
    String[] arguments = new String[0];

    Map<String, Command> commands = new LinkedHashMap<>();
    commands.put("test1", new TestCommand1(configuration, out));
    commands.put("test2", new TestCommand2(configuration, out));

    command = new HelpCommand(configuration, out, commands);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/HelpCommandOutput1.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testCommandHelp() throws Exception {
    String[] arguments = new String[]{"test1"};

    Map<String, Command> commands = new LinkedHashMap<>();
    commands.put("test1", new TestCommand1(configuration, out));

    command = new HelpCommand(configuration, out, commands);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/HelpCommandOutput2.txt"));

    assertThat(actual).isEqualTo(expected);
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
