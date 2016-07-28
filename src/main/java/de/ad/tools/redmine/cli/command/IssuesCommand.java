package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.RedmineUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.*;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class IssuesCommand extends RedmineCommand {

  static final String INVALID_PROJECT_MESSAGE = "'%s' is not a valid project.";
  static final String INVALID_PRIORITY_MESSAGE = "'%s' is not a valid priority.";
  static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE = "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE = "'%s' is not a valid tracker.";
  static final String INVALID_SORT_MESSAGE = "'%s' is not a valid sort.";

  private static final String NAME = "issues";
  private static final String DESCRIPTION = "Display issues.";
  private static final Argument[] ARGUMENTS = new Argument[0];
  private static final Option[] OPTIONS = new Option[] {
      new Option("project", "Only display issues for the specified project."),
      new Option("priority", "Only display issues with specified priority."),
      new Option("assignee", "Only display issues for the specified assignee."),
      new Option("status", "Only display issues with the specified status."),
      new Option("tracker", "Only display issues for the specified tracker."),
      new Option("sort", "Column to sort with. Append :desc to invert the order.")
  };

  private static final Map<String, Handler> handlers = new HashMap<>();

  public IssuesCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out,
        redmineManager);

    ArrayList<Handler> handlers = new ArrayList<>();
    handlers.add(new ProjectHandler());
    handlers.add(new PriorityHandler());
    handlers.add(new AssigneeHandler());
    handlers.add(new StatusHandler());
    handlers.add(new TrackerHandler());
    handlers.add(new SortHandler());

    for (Handler handler : handlers) {
      IssuesCommand.handlers.put(handler.getName(), handler);
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    Map<String, String> parameters = buildParameterMapFromOptions();

    IssueManager issueManager = redmineManager.getIssueManager();
    List<Issue> issues = issueManager.getIssues(parameters);

    final String[][] issueTable = new String[issues.size()][6];
    String[] header =
        new String[] { "ID", "Project", "Tracker", "Status", "Priority",
            "Assignee", "Updated",
            "Subject" };

    int i = 0;
    for (Issue issue : issues) {
      issueTable[i++] = buildRow(issue);
    }

    printTable(header, issueTable);
  }

  private Map<String, String> buildParameterMapFromOptions() throws Exception {
    HashMap<String, String>parameters = new HashMapDuplicates();

    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      handlers.get(option.getName())
          .handle(redmineManager, parameters, option.buildValue());
    }
    return parameters;
  }

  private String[] buildRow(Issue issue) {
    return new String[] { ""+issue.getId(),
        issue.getProject().getName(),
        issue.getTracker().getName(),
        issue.getStatusName(),
        issue.getPriorityText(),
        issue.getAssigneeName() != null ?
            issue.getAssigneeName() :
            "(not assigned)",
        getTimeDifferenceAsText(issue.getUpdatedOn()) +
            " ago",
        issue.getSubject()};
  }

  private static abstract class Handler {
    public abstract String getName();

    public abstract void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value) throws Exception;
  }

  private static class ProjectHandler extends Handler {

    @Override public String getName() {
      return "project";
    }

    @Override public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value) throws Exception {
      Optional<Project> project = RedmineUtil.resolveProjectByName(
          redmineManager, value);

      project.ifPresent(p -> parameters.put("project_id", String.valueOf(
          p.getId())));
      project.orElseThrow(
          () -> new Exception(String.format(INVALID_PROJECT_MESSAGE, value)));
    }
  }

  private static class PriorityHandler extends Handler {

    @Override public String getName() {
      return "priority";
    }

    @Override public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value) throws Exception {
      Optional<IssuePriority> priority =
          RedmineUtil.resolvePriorityByName(redmineManager, value);

      priority.ifPresent(p -> parameters.put("priority_id", String.valueOf(
          p.getId())));
      priority.orElseThrow(
          () -> new Exception(String.format(INVALID_PRIORITY_MESSAGE, value)));
    }
  }

  private static class AssigneeHandler extends Handler {

    @Override public String getName() {
      return "assignee";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      if ("me".equalsIgnoreCase(value) || value.matches("[0-9]+")) {
        HashMapDuplicates.addFormParameterEqual(parameters, "assigned_to_id", value);
      } else {
        throw new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value));
      }
    }
  }

  private static class StatusHandler extends Handler {

    @Override public String getName() {
      return "status";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      Optional<IssueStatus> status =
          RedmineUtil.resolveStatusByName(redmineManager, value);

      if (value.contains(",")) {
        String[] split = value.trim().split(",");
        for (int i = 0; i < split.length; i++) {
          String s = split[i];
          Optional<IssueStatus> statusSplit =
                  RedmineUtil.resolveStatusByName(redmineManager, s);
          if (statusSplit.isPresent()) {
            value = String.valueOf(statusSplit.get().getId());
            HashMapDuplicates.addFormParameterEqual(parameters, "status_id", value);
          }
        }
      } else if ("open".equalsIgnoreCase(value) || "close".equalsIgnoreCase(value)) {
        parameters.put("status_id", value);
      } else {
        if (status.isPresent()) {
          parameters.put("status_id", String.valueOf(status.get().getId()));
        } else {
          throw new Exception(String.format(INVALID_STATUS_MESSAGE, value));
        }
      }
    }

  }

  private static class TrackerHandler extends Handler {

    @Override public String getName() {
      return "tracker";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      Optional<Tracker> tracker =
          RedmineUtil.resolveTrackerByName(redmineManager, value);

      tracker.ifPresent(t -> parameters.put("tracker_id", String.valueOf(
          t.getId())));
      tracker.orElseThrow(() ->
          new Exception(String.format(INVALID_TRACKER_MESSAGE, value)));
    }
  }

  private static class SortHandler extends Handler {

    @Override public String getName() {
      return "sort";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      if (!StringUtils.isBlank(value)) {
        //TODO: Parse sort
        parameters.put("sort", value);
      } else {
        throw new Exception(String.format(INVALID_SORT_MESSAGE, value));
      }
    }
  }

}
