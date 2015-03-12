package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;

public class ConnectCommand extends Command {

  static final String ALREADY_CONNECTED_MESSAGE =
      "Cannot change connection when already connected to a server. Call 'reset' first.";
  static final String SUCCESS_MESSAGE =
      "Successfully connected user '%s' to server '%s'.";

  static final String FAILURE_MESSAGE =
      "Authorization failed. Did you provide a valid API key for server '%s'?";
  private static final String NAME = "connect";
  private static final String DESCRIPTION =
      "Connect to server using API key for authentication.";
  private static final Argument[] ARGUMENTS = new Argument[] {
      new Argument("url", "The redmine server url.", false),
      new Argument("apiKey", "The API key to use for authentication.",
          false) };

  private RedmineManagerFactory redmineManagerFactory;

  public ConnectCommand(Configuration configuration, PrintStream out,
      RedmineManagerFactory redmineManagerFactory) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out);

    this.redmineManagerFactory = redmineManagerFactory;
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    if (configuration.isConnected()) {
      println(ALREADY_CONNECTED_MESSAGE);
      return;
    }

    String url = getArguments()[0].getValue();
    String apiKey = getArguments()[1].getValue();

    try {
      String login = redmineManagerFactory.createWithApiKey(url, apiKey)
          .getUserManager().getCurrentUser().getLogin();

      configuration.setServer(url);
      configuration.setApiKey(apiKey);

      println(SUCCESS_MESSAGE, login, url);
    } catch (RedmineAuthenticationException e) {
      println(FAILURE_MESSAGE, url);
    }
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
