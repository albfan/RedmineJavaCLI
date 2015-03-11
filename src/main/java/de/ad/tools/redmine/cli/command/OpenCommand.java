package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import de.ad.tools.redmine.cli.Configuration;

import java.awt.*;
import java.io.PrintStream;
import java.net.URI;

public class OpenCommand extends RedmineCommand {

  private static final String NAME = "open";
  private static final String DESCRIPTION = "Open issue in default browser.";
  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new Argument("id", "The ID of the issue to open.", false) };

  public OpenCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out, redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    String id = getArguments()[0].getValue();

    if (!Desktop.isDesktopSupported()) {
      println("Cannot open issue '%s' in default browser.", id);
      return;
    }

    String uri =
        String.format("%s/issues/%s", configuration.getServer(), id);
    Desktop.getDesktop().browse(new URI(uri));
  }
}
