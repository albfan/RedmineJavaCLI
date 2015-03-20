package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.TemplateUtil;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
    Function table = o -> TemplateUtil.convertToTable((String) o);
    
    Map<String, Object> scope = new HashMap<>();
    scope.put("commands", commands.entrySet());
    scope.put("table", table);
    
    TemplateUtil.printTemplate(System.out, "HelpCommand1.template", scope);
  }

  private void printCommandHelp(Command command) {
    Function table = o -> TemplateUtil.convertToTable((String) o);

    Map<String, Object> scope = new HashMap<>();
    scope.put("command", command);
    scope.put("table", table);

    TemplateUtil.printTemplate(System.out, "HelpCommand2.template", scope);
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
