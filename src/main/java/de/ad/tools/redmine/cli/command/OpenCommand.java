package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

public class OpenCommand extends RedmineCommand {

  static final String NO_DESKTOP_SUPPORT_MESSAGE =
      "Cannot open issue '%s' in default browser.";

  private static final String NAME = "open";
  private static final String DESCRIPTION = "Open issue in default browser.";
  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new Argument("id", "The ID of the issue to open.", false) };

  private Browser browser;

  public OpenCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager, Browser browser) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out, redmineManager);

    this.browser = browser;
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    String id = getArguments()[0].getValue();

    assertBrowserIsSupported(id);

    String uri =
        String.format("%s/issues/%s", configuration.getServer(), id);
    browser.browse(new URI(uri));
  }

  private void assertBrowserIsSupported(String id) throws Exception {
    if (!browser.isSupported()) {
      throw new Exception(String.format(NO_DESKTOP_SUPPORT_MESSAGE, id));
    }
  }

  /**
   * Wraps static calls to Desktop for a better testability.
   */
  public static class Browser{

    public boolean isSupported() {
      return Desktop.isDesktopSupported();
    }

    public void browse(URI uri) throws IOException {
      Desktop.getDesktop().browse(uri);
    }
  }
}
