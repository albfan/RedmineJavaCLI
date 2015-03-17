package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;

class RedmineCommand extends Command {
  static final String NOT_CONNECTED_MESSAGE =
      "Cannot execute command '%s' when not connected to a server. Call 'connect' first.";
  protected final RedmineManager redmineManager;

  protected RedmineCommand(String name, String description,
      String longDescription, Argument[] arguments, Configuration configuration,
      PrintStream out, RedmineManager redmineManager) {
    super(name, description, longDescription, arguments, configuration, out);

    this.redmineManager = redmineManager;
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    if (!configuration.isConnected()) {
      throw new Exception(
          String.format(NOT_CONNECTED_MESSAGE, getName()));
    }
  }
}
