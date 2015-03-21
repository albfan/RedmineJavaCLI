package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CommandTest {
  private Configuration configuration;
  private PrintStream out;

  private Command command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    out = mock(PrintStream.class);
  }

  @Test
  public void testArgumentValidation() throws Exception {
    Command.Argument[] commandArguments =
        new Command.Argument[] { new Command.Argument("arg1", "test", false),
            new Command.Argument("arg2", "test", false) };

    command = new Command("test", "This is a test command.", "Long Description",
        commandArguments, configuration, out);

    String[] arguments = new String[] { "val1", "val2" };

    command.process(arguments);
  }

  @Test
  public void testArgumentValidationWithTooFewArguments() throws Exception {
    Command.Argument[] commandArguments =
        new Command.Argument[] { new Command.Argument("arg1", "test", false),
            new Command.Argument("arg2", "test", false) };

    command = new Command("test", "This is a test command.", "Long Description",
        commandArguments, configuration, out);

    String message =
        String.format(Command.TOO_FEW_ARGUMENTS_MESSAGE, command.getName(), 2,
            1);

    String[] arguments = new String[] { "val1" };

    exception.expect(IllegalArgumentException.class);
    exception.expectMessage(message);
    command.process(arguments);
  }

  @Test
  public void testArgumentValidationWithTooManyArguments() throws Exception {
    Command.Argument[] commandArguments =
        new Command.Argument[] { new Command.Argument("arg1", "test", false),
            new Command.Argument("arg2", "test", false) };

    command = new Command("test", "This is a test command.", "Long Description",
        commandArguments, configuration, out);

    String message =
        String.format(Command.TOO_MANY_ARGUMENTS_MESSAGE, command.getName(), 2,
            3);

    String[] arguments = new String[] { "val1", "val2", "val3" };

    exception.expect(IllegalArgumentException.class);
    exception.expectMessage(message);
    command.process(arguments);
  }

  @Test
  public void testOptionValidation() throws Exception {
    Command.Argument[] commandArguments = new Command.Argument[0];

    Command.Option[] commandOptions = new Command.Option[] {
        new Command.Option("opt", "This is an option")
    };

    command = new Command("test", "This is a test command.", "Long Description",
        commandArguments, commandOptions, configuration, out);

    String[] arguments = new String[] { "--opt=v" };

    command.process(arguments);
  }

  @Test
  public void testOptionValidationWithInvalidOption() throws Exception {
    Command.Argument[] commandArguments = new Command.Argument[0];

    Command.Option[] commandOptions = new Command.Option[] {
        new Command.Option("opt", "This is an option")
    };

    command = new Command("test", "This is a test command.", "Long Description",
        commandArguments, commandOptions, configuration, out);

    String[] arguments = new String[] { "--op=v" };

    String message = String.format(Command.INVALID_OPTION_MESSAGE, "--op=v");

    exception.expect(IllegalArgumentException.class);
    exception.expectMessage(message);
    command.process(arguments);
  }
}
