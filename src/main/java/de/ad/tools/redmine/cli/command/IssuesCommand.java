package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.StringUtil;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class IssuesCommand extends RedmineCommand {

  private static final String NAME = "issues";
  private static final String DESCRIPTION = "Display issues.";
  private static final Argument[] ARGUMENTS = new Argument[0];

  public IssuesCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out,
        redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    IssueManager issueManager = redmineManager.getIssueManager();

    //Parameter spec: http://www.redmine.org/projects/redmine/wiki/Rest_Issues
    //Currently not working, see issue:
    //https://github.com/taskadapter/redmine-java-api/issues/181
    Map<String, String> parameters = new HashMap<String, String>();
    List<Issue> issues = issueManager.getIssues(parameters);

    String[][] issueTable = new String[issues.size()][6];
    String[] header =
        new String[] { "ID", "Tracker", "Status", "Priority",
            "Assignee", "Updated",
            "Subject" };

    int i = 0;
    for (Issue issue : issues) {
      issueTable[i++] =
          new String[] { "#" + issue.getId(),
              issue.getTracker().getName(),
              issue.getStatusName(),
              issue.getPriorityText(),
              issue.getAssignee() != null ?
                  issue.getAssignee().getFullName() :
                  "(not assigned)",
              getTimeDifferenceAsText(issue.getUpdatedOn()) +
                  " ago",
              StringUtil.ellipsize(issue.getSubject(), 24) };
    }

    printTable(header, issueTable);
  }
}
