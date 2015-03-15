package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.StringUtil;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class StatusesCommand extends RedmineCommand {

  private static final String NAME = "statuses";
  private static final String DESCRIPTION = "Display available issue statues.";
  private static final Argument[] ARGUMENTS = new Argument[0];

  public StatusesCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out, redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    IssueManager issueManager = redmineManager.getIssueManager();

    List<IssueStatus> statuses = issueManager.getStatuses();

    String[][] statusesTable = new String[statuses.size()][4];
    String[] header =
        new String[] { "Name", "ID", "Default", "Closed" };

    int i = 0;
    for (IssueStatus status : statuses) {
      statusesTable[i++] =
          new String[] { status.getName(), String.valueOf(status.getId()),
              status.isDefaultStatus() ? "X" : "",
              status.isClosed() ? "X" : "" };
    }

    printTable(header, statusesTable);
  }
}
