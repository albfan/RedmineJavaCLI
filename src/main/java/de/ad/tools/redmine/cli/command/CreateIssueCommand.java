package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class CreateIssueCommand extends RedmineCommand {
  static final String SUCCESS_MESSAGE = "Issue #%d succesfully created.";

  static final String INVALID_PRIORITY_MESSAGE =
      "'%s' is not a valid priority.";
  static final String INVALID_ASSIGNEE_MESSAGE =
      "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE =
      "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE =
      "'%s' is not a valid tracker.";

  private static final String NAME = "create-issue";
  private static final String DESCRIPTION = "Create a new issue.";
  private static final Argument[] ARGUMENTS =
      new Argument[] { new Argument("projectKey",
          "The key of the project to add this issue to.",
          false), new Argument("subject", "The subject of the issue.",
          false) };
  private static final Option[] OPTIONS = new Option[] {
      new Option("description", "The description of the issue to create."),
      new Option("priority", "The priority of the issue to create."),
      new Option("assignee", "The assignee of the issue to create."),
      new Option("status", "The status of the issue to create."),
      new Option("tracker", "The tracker of the issue to create.") };

  private static final Map<String, Handler> handlers = new HashMap<>();

  public CreateIssueCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out,
        redmineManager);

    Handler description = new DescriptionHandler();
    Handler priority = new PriorityHandler();
    Handler assignee = new AssigneeHandler();
    Handler status = new StatusHandler();
    Handler tracker = new TrackerHandler();

    handlers.put(description.getName(), description);
    handlers.put(priority.getName(), priority);
    handlers.put(assignee.getName(), assignee);
    handlers.put(status.getName(), status);
    handlers.put(tracker.getName(), tracker);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);
    String projectKey = getArguments()[0].getValue();
    String subject = getArguments()[1].getValue();

    ProjectManager projectManager = redmineManager.getProjectManager();
    IssueManager issueManager = redmineManager.getIssueManager();

    Project project = projectManager.getProjectByKey(projectKey);

    Issue issueToCreate =
        IssueFactory.create(project.getId(), subject);

    for (Option option : getOptions()) {
      if (option.getValue() == null) {
        continue;
      }

      handlers.get(option.getName())
          .handle(redmineManager, issueToCreate, option.getValue());
    }

    Issue newIssue = issueManager.createIssue(issueToCreate);

    println(SUCCESS_MESSAGE, newIssue.getId());
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

  private static class PriorityHandler extends Handler {

    @Override public String getName() {
      return "priority";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      List<IssuePriority> priorities =
          redmineManager.getIssueManager().getIssuePriorities();

      Optional<IssuePriority> newPriority = priorities.stream()
          .filter(p -> value.equals(p.getName()))
          .findFirst();

      newPriority.ifPresent(p -> issue.setPriorityId(p.getId()));
      newPriority.orElseThrow(
          () -> new Exception(String.format(INVALID_PRIORITY_MESSAGE, value)));
    }
  }

  private static class AssigneeHandler extends Handler {

    @Override public String getName() {
      return "assignee";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      List<Membership> memberships =
          redmineManager.getMembershipManager()
              .getMemberships(issue.getProject().getId());

      Optional<Membership> newAssignee = memberships.stream()
          .filter(m -> value.equals(m.getUser().getFullName()))
          .findFirst();

      newAssignee.ifPresent(m -> issue.setAssignee(m.getUser()));
      newAssignee.orElseThrow(
          () -> new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value)));
    }
  }

  private static class StatusHandler extends Handler {

    @Override public String getName() {
      return "status";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      List<IssueStatus> statuses =
          redmineManager.getIssueManager().getStatuses();

      Optional<IssueStatus> newStatus =
          statuses.stream().filter(s -> value.equals(s.getName())).findFirst();

      newStatus.ifPresent(s -> issue.setStatusId(s.getId()));
      newStatus.orElseThrow(
          () -> new Exception(String.format(INVALID_STATUS_MESSAGE, value)));
    }
  }

  private static class TrackerHandler extends Handler {

    @Override public String getName() {
      return "tracker";
    }

    @Override
    public void handle(RedmineManager redmineManager, Issue issue, String value)
        throws Exception {
      List<Tracker> trackers = redmineManager.getIssueManager().getTrackers();

      Optional<Tracker> newTracker = trackers.stream()
          .filter(t -> value.equals(t.getName())).findFirst();

      newTracker.ifPresent(issue::setTracker);
      newTracker.orElseThrow(() ->
          new Exception(String.format(INVALID_TRACKER_MESSAGE, value)));
    }
  }
}
