package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;

public class ConnectCommand extends Command {

  private static final String NAME = "connect";
  private static final String DESCRIPTION =
      "Connect to server using API key for authentication.";
  private static final Argument[] ARGUMENTS = new Argument[] {
      new Argument("url", "The redmine server url.", false),
      new Argument("apiKey", "The API key to use for authentication.",
          false) };

  public ConnectCommand(Configuration configuration, PrintStream out) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    if (configuration.isConnected()) {
      println("Cannot change connection when already connected to a " +
          "server. Call 'reset' first.");
      return;
    }

    String url = getArguments()[0].getValue();
    String apiKey = getArguments()[1].getValue();

    try {
      String login = RedmineManagerFactory.createWithApiKey(url, apiKey)
          .getUserManager().getCurrentUser().getLogin();

      configuration.setServer(url);
      configuration.setApiKey(apiKey);

      println("Successfully connected user '%s' to server '%s'.", login,
          url);
    } catch (RedmineAuthenticationException e) {
      println(
          "Authorization failed. Did you provide a valid API key for server '%s'?",
          url);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
