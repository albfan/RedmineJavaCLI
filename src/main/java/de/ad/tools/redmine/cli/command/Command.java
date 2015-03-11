package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.PrintUtil;

import java.io.PrintStream;

public class Command {
  static final String TOO_FEW_ARGUMENTS_MESSAGE =
      "Command '%s' requires %d argument(s). Found %d.";
  static final String TOO_MANY_ARGUMENTS_MESSAGE =
      "Command '%s' accepts up to %d argument(s). Found %d.";

  private final String name;
  private final String description;
  private final Argument[] arguments;

  protected final Configuration configuration;
  private final PrintStream out;

  protected Command(String name, String description, Argument[] arguments,
      Configuration configuration, PrintStream out) {
    this.name = name;
    this.description = description;
    this.arguments = arguments;
    this.configuration = configuration;
    this.out = out;
  }

  public void process(String[] arguments) throws Exception {
    validateArguments(arguments);
    assignArguments(arguments);
  }

  public final String getName() {
    return name;
  }

  public final String getDescription() {
    return description;
  }

  public final Argument[] getArguments() {
    return arguments;
  }

  protected final void println(String s, Object... args) {
    out.println(String.format(s, args));
  }

  protected final void println() {
    out.println();
  }

  protected final void printTable(String[][] table) {
    PrintUtil.printTable(out, table);
  }

  protected final void printTable(String[] header, String[][] table) {
    PrintUtil.printTable(out, header, table);
  }

  private void validateArguments(String[] arguments) throws Exception {
    int requiredArgumentsCount = getRequiredArgumentsCount();
    int argumentCount = getArguments().length;

    if (requiredArgumentsCount > arguments.length) {
      throw new IllegalArgumentException(
          String.format(
              TOO_FEW_ARGUMENTS_MESSAGE,
              getName(),
              requiredArgumentsCount, arguments.length));
    } else if (argumentCount < arguments.length) {
      throw new IllegalArgumentException(
          String.format(
              TOO_MANY_ARGUMENTS_MESSAGE,
              getName(),
              argumentCount, arguments.length));
    }
  }

  private void assignArguments(String[] arguments) {
    Argument[] commandArguments = getArguments();
    for (int i = 0; i < arguments.length; i++) {
      commandArguments[i].setValue(arguments[i]);
    }
  }

  private int getRequiredArgumentsCount() {
    Argument[] arguments = getArguments();
    int requiredArgumentsCount = arguments.length;

    for (Argument argument : arguments) {
      if (argument.isOptional()) {
        requiredArgumentsCount--;
      }
    }

    return requiredArgumentsCount;
  }

  public static final class Argument {
    private final String name;
    private final String description;
    private final boolean isOptional;
    private String value;

    public Argument(String name, String description, boolean isOptional) {
      this.name = name;
      this.description = description;
      this.isOptional = isOptional;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public boolean isOptional() {
      return isOptional;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
