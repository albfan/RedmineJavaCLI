package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import de.ad.tools.redmine.cli.Configuration;
import java.awt.Desktop;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateIssueCommand extends RedmineCommand {

  static final String INVALID_KEYVALUE_MESSAGE =
      "'%s' is not valid key-value assignment. Please use key=value.";
  static final String INVALID_KEY_MESSAGE =
      "'%s' is not a valid key.";
  static final String INVALID_ASSIGNEE_MESSAGE =
      "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE =
      "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE =
      "'%s' is not a valid tracker.";
  static final String ISSUE_UPDATE_SUCCESS_MESSAGE =
      "Sucessfully updated issue #%d.";

  private static final String NAME = "update-issue";
  private static final String DESCRIPTION = "Update a given issue.";
  private static final String LONG_DESCRIPTION =
      "Supported keys:\n" +
          " - description, subject, assignee, status, tracker\n\n";

  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new Argument("id", "The ID of the issue you want to update.",
              false),
          new Argument("keyValue",
              "The key you want to update with value, separated by '='.",
              false) };

  private static final Map<String, Handler> handlers = new HashMap<>();

  public UpdateIssueCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, LONG_DESCRIPTION, ARGUMENTS, configuration, out,
        redmineManager);

    Handler description = new DescriptionHandler();
    Handler subject = new SubjectHandler();
    Handler assignee = new AssigneeHandler();
    Handler status = new StatusHandler();
    Handler tracker = new TrackerHandler();

    handlers.put(description.getName(), description);
    handlers.put(subject.getName(), subject);
    handlers.put(assignee.getName(), assignee);
    handlers.put(status.getName(), status);
    handlers.put(tracker.getName(), tracker);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    String id = getArguments()[0].getValue();
    String keyValue = getArguments()[1].getValue();

    if (!keyValue.contains("=")) {
      throw new Exception(String.format(INVALID_KEYVALUE_MESSAGE, keyValue));
    }

    processIssue(id, keyValue);
  }

  private void processIssue(String id, String keyValue) throws Exception {
    IssueManager issueManager = redmineManager.getIssueManager();
    Issue issue = issueManager.getIssueById(Integer.valueOf(id));

    String[] keyAndValue = keyValue.split("=");
    String key = keyAndValue[0];
    String value = stripQuotes(keyAndValue[1]);

    if (handlers.containsKey(key)) {
      handlers.get(key).handle(redmineManager, issue, value);
    } else {
      throw new Exception(String.format(INVALID_KEY_MESSAGE, key));
    }

    issueManager.update(issue);

    println(ISSUE_UPDATE_SUCCESS_MESSAGE, issue.getId());
  }

  private String stripQuotes(String s) {
    if (s.startsWith("\"")) {
      s = s.substring(1);
    }

    if (s.endsWith("\"")) {
      s = s.substring(0, s.length() - 1);
    }

    return s;
  }

  private static abstract class Handler {
    public abstract String getName();

    public abstract void handle(RedmineManager redmineManager, Issue issue,
        String value) throws Exception;
  }

  private static class DescriptionHandler extends Handler {

    @Override public String getName() {
      return "description";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      issue.setDescription(value);
    }
  }

  private static class SubjectHandler extends Handler {

    @Override public String getName() {
      return "subject";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      issue.setSubject(value);
    }
  }

  private static class AssigneeHandler extends Handler {

    @Override public String getName() {
      return "assignee";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      int projectId = issue.getProject().getId();
      Project currentProject = null;
      List<Project> projects = redmineManager.getProjectManager().getProjects();
      for (Project project : projects) {
        if (projectId == project.getId()) {
          currentProject = project;
          break;
        }
      }

      List<Membership> memberships =
          redmineManager.getMembershipManager().getMemberships(
              currentProject.getIdentifier());

      User newAssignee = null;
      for (Membership membership : memberships) {
        if (value.equals(membership.getUser().getFullName())) {
          newAssignee = membership.getUser();
        }
      }

      if (newAssignee != null) {
        issue.setAssignee(newAssignee);
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
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      IssueStatus newStatus = null;
      List<IssueStatus> statuses =
          redmineManager.getIssueManager().getStatuses();
      for (IssueStatus status : statuses) {
        if (value.equals(status.getName())) {
          newStatus = status;
          break;
        }
      }

      if (newStatus != null) {
        issue.setStatusId(newStatus.getId());
      } else {
        throw new Exception(String.format(INVALID_STATUS_MESSAGE, value));
      }
    }
  }

  private static class TrackerHandler extends Handler {

    @Override public String getName() {
      return "tracker";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      Tracker newTracker = null;
      List<Tracker> trackers = redmineManager.getIssueManager().getTrackers();
      for (Tracker tracker : trackers) {
        if (value.equals(tracker.getName())) {
          newTracker = tracker;
          break;
        }
      }

      if (newTracker != null) {
        issue.setTracker(newTracker);
      } else {
        throw new Exception(String.format(INVALID_TRACKER_MESSAGE, value));
      }
    }
  }
}
