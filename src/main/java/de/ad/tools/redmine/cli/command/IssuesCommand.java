package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.RedmineUtil;
import de.ad.tools.redmine.cli.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.*;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class IssuesCommand extends RedmineCommand {

  static final String INVALID_PROJECT_MESSAGE =
      "'%s' is not a valid project.";
  static final String INVALID_PRIORITY_MESSAGE =
      "'%s' is not a valid priority.";
  static final String INVALID_ASSIGNEE_MESSAGE =
      "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE =
      "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE =
      "'%s' is not a valid tracker.";

  private static final String NAME = "issues";
  private static final String DESCRIPTION = "Display issues.";
  private static final Argument[] ARGUMENTS = new Argument[0];
  private static final Option[] OPTIONS = new Option[] {
      new Option("project", "Only display issues for the specified project."),
      new Option("priority", "Only display issues with specified priority."),
      new Option("assignee", "Only display issues for the specified assignee."),
      new Option("status", "Only display issues with the specified status."),
      new Option("tracker", "Only display issues for the specified tracker.")
  };

  private static final Map<String, Handler> handlers = new HashMap<>();

  public IssuesCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out,
        redmineManager);

    Handler project = new ProjectHandler();
    Handler priority = new PriorityHandler();
    Handler assignee = new AssigneeHandler();
    Handler status = new StatusHandler();
    Handler tracker = new TrackerHandler();

    handlers.put(project.getName(), project);
    handlers.put(priority.getName(), priority);
    handlers.put(assignee.getName(), assignee);
    handlers.put(status.getName(), status);
    handlers.put(tracker.getName(), tracker);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    Map<String, String> parameters = buildParameterMapFromOptions();

    IssueManager issueManager = redmineManager.getIssueManager();
    List<Issue> issues = issueManager.getIssues(parameters);

    final String[][] issueTable = new String[issues.size()][6];
    String[] header =
        new String[] { "ID", "Tracker", "Status", "Priority",
            "Assignee", "Updated",
            "Subject" };

    int i = 0;
    for (Issue issue : issues) {
      issueTable[i++] = buildRow(issue);
    }

    printTable(header, issueTable);
  }

  private Map<String, String> buildParameterMapFromOptions() throws Exception {
    //Parameter spec: http://www.redmine.org/projects/redmine/wiki/Rest_Issues

    HashMap<String, String>parameters = new HashMap<String, String>() {

      Set<Entry<String, String>> entries;
      @Override
      public Set<Entry<String, String>> entrySet() {
        if (entries == null) {
          entries = new AbstractSet<Entry<String, String>>() {

            ArrayList<Entry<String, String>> list = new ArrayList<>();
            @Override
            public Iterator<Entry<String, String>> iterator() {
              return list.iterator();
            }

            @Override
            public int size() {
              return list.size();
            }

            @Override
            public boolean add(Entry<String, String> stringStringEntry) {
              return list.add(stringStringEntry);
            }
          };
        }
        return entries;
      }

      @Override
      public int size() {
        return entries.size();
      }

      public String put(String key, String value) {
        Set<Entry<String, String>> entries = entrySet();
        StatusHandler.MyEntry entry = new StatusHandler.MyEntry();
        entry.setKey(key);
        entry.setValue(value);
        entries.add(entry);
        return value;
      }
    };

    for (Option option : getOptions()) {
      if (option.getValue() == null) {
        continue;
      }

      handlers.get(option.getName())
          .handle(redmineManager, parameters, option.getValue());
    }
    return parameters;
  }

  private String[] buildRow(Issue issue) {
    return new String[] { "#" + issue.getId(),
        issue.getTracker().getName(),
        issue.getStatusName(),
        issue.getPriorityText(),
        issue.getAssignee() != null ?
            issue.getAssignee().getFullName() :
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
        parameters.put("assigned_to_id", value);
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
        parameters.put("f[]","status_id");
        parameters.put("op[status_id]","=");
        for (int i = 0; i < split.length; i++) {
          String s = split[i];
          Optional<IssueStatus> statusSplit =
                  RedmineUtil.resolveStatusByName(redmineManager, s);
          if (statusSplit.isPresent()) {
            parameters.put("v[status_id][]", String.valueOf(statusSplit.get().getId()));
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

    private static class MyEntry implements Map.Entry<String, String> {
      String key;
      String value;

      @Override
      public String getKey() {
        return key;
      }

      @Override
      public String getValue() {
        return value;
      }

      @Override
      public String setValue(String value) {
        this.value = value;
        return value;
      }

      public void setKey(String key) {
        this.key = key;
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
}
