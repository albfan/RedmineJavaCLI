package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;
import java.util.Map;

public class HelpCommand extends Command {
  static final String NOT_A_REDMINE_COMMAND_MESSAGE =
      "'%s' is not a redmine command. Call 'help' to see available commands.";
  
  private static final String NAME = "help";
  private static final String DESCRIPTION =
      "Display general help or (if provided) command help.";
  private static final Argument[] ARGUMENTS = new Argument[] {
      new Argument("command", "The command to show help.", true) };

  private final Map<String, Command> commands;

  public HelpCommand(Configuration configuration, PrintStream out,
      Map<String, Command> commands) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);

    this.commands = commands;
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    if (arguments.length == 1) {
      printCommandHelp(getArguments()[0].getValue());
    } else {
      printGeneralHelp();
    }
  }

  private void printGeneralHelp() {
    println("usage: redmine <command> [<args>]");
    println();

    String[][] help = new String[commands.size()][3];
    int i = 0;
    for (Map.Entry<String, Command> commandEntry : commands.entrySet()) {
      Command command = commandEntry.getValue();

      help[i++] = new String[] { command.getName(),
          createArgumentHelpString(command),
          command.getDescription() };
    }

    printTable(help);
  }

  private void printCommandHelp(Command command) {
    String arguments = createArgumentHelpString(command);

    printHeading("Command");
    println(command.getName());
    println();
    printHeading("Description");
    println(command.getDescription());
    println(command.getLongDescription());
    println();

    println("usage: redmine %s %s", command.getName(), arguments);
    println();

    printArgumentHelp(command);
  }

  private void printArgumentHelp(Command command) {
    String[][] help = new String[command.getArguments().length][2];
    int i = 0;
    for (Argument argument : command.getArguments()) {
      String formattedArgumentName =
          String.format("<%s>", argument.getName());
      help[i++] = new String[] { formattedArgumentName,
          argument.getDescription() };
    }

    printTable(help);
  }

  private void printCommandHelp(String commandName) {
    if (commands.containsKey(commandName)) {
      printCommandHelp(commands.get(commandName));
    } else {
      println(NOT_A_REDMINE_COMMAND_MESSAGE, commandName);
    }
  }

  private String createArgumentHelpString(Command command) {
    StringBuilder argumentBuilder = new StringBuilder();

    for (Command.Argument argument : command.getArguments()) {
      if (argument.isOptional()) {
        argumentBuilder
            .append(String.format("[<%s>] ", argument.getName()));
      } else {
        argumentBuilder
            .append(String.format("<%s> ", argument.getName()));
      }
    }

    return argumentBuilder.toString();
  }
}
