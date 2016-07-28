package de.ad.tools.redmine.cli;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.command.*;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ini4j.Ini;

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

    String uri = null;
    String apiKey = null;
    if (configuration.isConfigured()) {
      uri = configuration.getServer();
      apiKey = configuration.getApiKey();
      redmineManager = redmineManagerFactory.createWithApiKey(uri, apiKey);
    }

    ArrayList<Command> commandList = new ArrayList<>();
    commandList.add(new HelpCommand(configuration, out, commands));
    commandList.add(new ConnectCommand(configuration, out, redmineManagerFactory));
    commandList.add(new ProjectsCommand(configuration, out, redmineManager));
    commandList.add(new ProjectCommand(configuration, out, redmineManager));
    commandList.add(new IssuesCommand(configuration, out, redmineManager));
    commandList.add(new IssueCommand(configuration, out, redmineManager));
    commandList.add(new HistoryCommand(configuration, out, redmineManager));
    commandList.add(new ListCommand(configuration, out, redmineManager));
    commandList.add(new CreateIssueCommand(configuration, out, redmineManager));
    commandList.add(new UpdateIssueCommand(configuration, out, redmineManager));
    commandList.add(new OpenCommand(configuration, out, redmineManager, new OpenCommand.Browser()));
    commandList.add(new ResetCommand(configuration, out));
    commandList.add(new AliasCommand(configuration, out));
    commandList.add(new ConfigCommand(configuration, out));
    commandList.add(new TimeEntriesCommand(configuration, out, redmineManager));

    for (Command command : commandList) {
      commands.put(command.getName(), command);
    }
  }

  public void handleCommand(String[] args) throws Exception {
    validateArguments(args);

    handleCommandInternal(args);
  }

  private void handleCommandInternal(String[] args) throws Exception {
    String command = getCommand(args);
    String[] arguments = getArguments(args);
    Ini ini = new Ini(new File(Application.CONFIGURATION_FILE_NAME));
    String aliasCommand = ini.containsKey("alias") ? ini.get("alias").get(command) : null;
    if (aliasCommand != null) {
      args = aliasCommand.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); //TODO: alias with quoted spaces
      command = getCommand(args);
      String[] argumentsAlias = getArguments(args);
      ArrayList<String> argumentList = new ArrayList<>();
      argumentList.addAll(Arrays.asList(argumentsAlias));
      argumentList.addAll(Arrays.asList(arguments));
      arguments = argumentList.toArray(new String[]{});
    }

    if (StringUtils.isNumeric(command)) {
      ArrayList<String> argumentsList = new ArrayList<>(Arrays.asList(arguments));
      argumentsList.add(0, command);
      arguments = argumentsList.toArray(new String[argumentsList.size()]);
      command = "issue";
    } else {
      validateCommand(command);
    }

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
      throw new Exception(String.format(INVALID_COMMAND_MESSAGE, command));
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
