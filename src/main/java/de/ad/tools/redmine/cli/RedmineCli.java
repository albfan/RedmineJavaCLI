package de.ad.tools.redmine.cli;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import de.ad.tools.redmine.cli.command.*;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RedmineCli {
  static final String INVALID_ARGUMENT_MESSAGE =
      "No arguments supplied. Call 'help' to see available commands.";
  public static final String INVALID_COMMAND_MESSAGE =
      "'%s' is not a redmine command. Call 'help' to see available commands.";

  private Configuration configuration;
  private transient Map<String, Command> commands;
  private PrintStream out;
  private RedmineManagerFactory redmineManagerFactory;

  public RedmineCli(Configuration configuration, PrintStream out,
      RedmineManagerFactory redmineManagerFactory) {
    this.configuration = configuration;
    this.out = out;
    this.redmineManagerFactory = redmineManagerFactory;

    initCommands();
  }

  private void initCommands() {
    commands = new LinkedHashMap<>();

    RedmineManager redmineManager = null;

    if (configuration.isConnected()) {
      redmineManager = redmineManagerFactory
          .createWithApiKey(configuration.getServer(),
              configuration.getApiKey());
    }

    Command help = new HelpCommand(configuration, out, commands);
    Command connect = new ConnectCommand(configuration, out,
        redmineManagerFactory);
    Command projects =
        new ProjectsCommand(configuration, out, redmineManager);
    Command project =
        new ProjectCommand(configuration, out, redmineManager);
    Command issues = new IssuesCommand(configuration, out, redmineManager);
    Command issue = new IssueCommand(configuration, out, redmineManager);
    Command history =
        new HistoryCommand(configuration, out, redmineManager);
    Command list = new ListCommand(configuration, out, redmineManager);
    Command open = new OpenCommand(configuration, out, redmineManager,
        new OpenCommand.Browser());
    Command reset = new ResetCommand(configuration, out);

    commands.put(help.getName(), help);
    commands.put(connect.getName(), connect);
    commands.put(projects.getName(), projects);
    commands.put(project.getName(), project);
    commands.put(issues.getName(), issues);
    commands.put(issue.getName(), issue);
    commands.put(history.getName(), history);
    commands.put(open.getName(), open);
    commands.put(list.getName(), list);
    commands.put(reset.getName(), reset);
  }

  public void handleCommand(String[] args) throws Exception {
    validateArguments(args);

    handleCommandInternal(args);
  }

  private void handleCommandInternal(String[] args) throws Exception {
    String command = getCommand(args);
    String[] arguments = getArguments(args);

    validateCommand(command);

    proccessCommand(command, arguments);
  }

  private void proccessCommand(String commandName, String[] arguments)
      throws Exception {
    Command command = commands.get(commandName);

    command.process(arguments);
  }

  private void validateArguments(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      throw new Exception(
          INVALID_ARGUMENT_MESSAGE);
    }
  }

  private String[] getArguments(String[] args) {
    return Arrays.copyOfRange(args, 1, args.length);
  }

  private static String getCommand(String[] args) {
    return args[0];
  }

  private void validateCommand(String command) throws Exception {
    if (!commands.containsKey(command)) {
      throw new Exception(
          String.format(INVALID_COMMAND_MESSAGE,
              command));
    }
  }

  @Override public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, false);
  }

  @Override public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, false);
  }

  /**
   * Wraps static calls to RedmineManagerFactory for a better testability.
   */
  public static class RedmineManagerFactory {

    public RedmineManager createWithApiKey(String url, String apiKey) {
      return com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(
          url, apiKey);
    }
  }
}
