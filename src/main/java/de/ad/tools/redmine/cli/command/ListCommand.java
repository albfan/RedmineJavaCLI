package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import java.util.List;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class ListCommand extends RedmineCommand {

  static final String INVALID_ENTITY_MESSAGE =
      "'%s' could not be recognized.";

  private static final String NAME = "list";
  private static final String DESCRIPTION = "List the specified entity.";
  private static final String LONG_DESCRIPTION =
      "Currently supported entities are:\nstatus, tracker";
  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new Argument("entity", "The entity you want to list.", false) };

  public ListCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, LONG_DESCRIPTION, ARGUMENTS, configuration, out,
        redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    String entity = getArguments()[0].getValue();

    switch (entity) {
      case "status":
        listStatus();
        break;
      case "tracker":
        listTracker();
        break;
      default:
        throw new Exception(String.format(INVALID_ENTITY_MESSAGE, entity));
    }
  }

  private void listStatus() throws Exception {
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

  private void listTracker() throws Exception {
    IssueManager issueManager = redmineManager.getIssueManager();

    List<Tracker> trackers = issueManager.getTrackers();

    String[][] trackerTable = new String[trackers.size()][2];
    String[] header = new String[] { "Name", "ID" };

    int i = 0;
    for (Tracker tracker : trackers) {
      trackerTable[i++] =
          new String[] { tracker.getName(), String.valueOf(tracker.getId()) };
    }

    printTable(header, trackerTable);
  }
}
