package de.ad.tools.redmine.cli.command;

import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;

public class ResetCommand extends Command {
  static final String RESET_SUCCESS_MESSAGE =
      "Successfully reset configuration.";

  private static final String NAME = "reset";
  private static final String DESCRIPTION =
      "Reset the current configuration.";
  private static final Argument[] ARGUMENTS = new Argument[0];

  public ResetCommand(Configuration configuration, PrintStream out) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out);
  }

  @Override
  public void process(String[] arguments) {
    configuration.reset();

    println(RESET_SUCCESS_MESSAGE);
  }
}
