package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.RedmineUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpdateIssueCommand extends RedmineCommand {

  static final String INVALID_PRIORITY_MESSAGE = "'%s' is not a valid priority.";
  static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE = "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE = "'%s' is not a valid tracker.";
  static final String INVALID_NOTES_MESSAGE = "'%s' is not a valid note.";
  static final String ISSUE_UPDATE_SUCCESS_MESSAGE = "Sucessfully updated issue #%d.";
  static final String NO_OPTION_SET_MESSAGE = "At least one option needs to be set. See 'help %s' for more information.";

  private static final String NAME = "update-issue";
  private static final String DESCRIPTION = "Update a given issue.";
  private static final String LONG_DESCRIPTION = "";

  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new NumberArgument("id", "The ID of the issue you want to update.",
              false) };
  private static final Option[] OPTIONS = new Option[] {
      new Option("description", "The description of the issue to update."),
      new Option("subject", "The subject of the issue to update."),
      new Option("priority", "The priority of the issue to update."),
      new Option("assignee", "The assignee of the issue to update."),
      new Option("status", "The status of the issue to update."),
      new Option("tracker", "The tracker of the issue to update."),
      new Option("doneratio", "The done ratio of the issue to update."),
      new Option("notes", "Comments about the update."),
      Option.buildOptionWithoutValue("private-issue", "Issue is private."),
      Option.buildOptionWithoutValue("private-notes", "Comment is private.") };

  private static final Map<String, Handler> handlers = new HashMap<>();

  public UpdateIssueCommand(Configuration configuration, PrintStream out, RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, LONG_DESCRIPTION, ARGUMENTS, OPTIONS, configuration, out, redmineManager);

    ArrayList<Handler> handlersList = new ArrayList<>();
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "description";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        issue.setDescription(value);
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "subject";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        issue.setSubject(value);
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "priority";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        Optional<IssuePriority> newPriority =
            RedmineUtil.resolvePriorityByName(redmineManager, value);

        newPriority.ifPresent(p -> issue.setPriorityId(p.getId()));
        newPriority.orElseThrow(
            () -> new Exception(String.format(INVALID_PRIORITY_MESSAGE, value)));
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "assignee";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        Optional<Membership> newAssignee =
            RedmineUtil.resolveMembershipByName(redmineManager,
                issue.getProjectId(), value);

        newAssignee.ifPresent(m -> issue.setAssigneeId(m.getUserId()));
        newAssignee.orElseThrow(
            () -> new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value)));
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "status";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        Optional<IssueStatus> newStatus =
            RedmineUtil.resolveStatusByName(redmineManager, value);

        newStatus.ifPresent(s -> issue.setStatusId(s.getId()));
        newStatus.orElseThrow(
            () -> new Exception(String.format(INVALID_STATUS_MESSAGE, value)));
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "tracker";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        Optional<Tracker> newTracker =
            RedmineUtil.resolveTrackerByName(redmineManager, value);

        newTracker.ifPresent(issue::setTracker);
        newTracker.orElseThrow(() ->
            new Exception(String.format(INVALID_TRACKER_MESSAGE, value)));
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "doneratio";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        try {
          int doneratio = Integer.parseInt(value);
          if (doneratio >= 0 && doneratio <= 100) {
            issue.setDoneRatio(doneratio);
          } else {
            throw new Exception("done ratio invalid value");
          }
        } catch (Exception e ) {
          throw new Exception(String.format(INVALID_TRACKER_MESSAGE, value));
        }
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "notes";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
          throws Exception {
        if (!StringUtils.isBlank(value)) {
          issue.setNotes(value);
        }else {
            throw new Exception(String.format(INVALID_NOTES_MESSAGE, value));
        }
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "private-notes";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
              throws Exception {
        issue.setPrivateNotes(true);
      }
    });
    handlersList.add(new Handler() {
      @Override public String getName() {
        return "private-issue";
      }

      @Override
      public void handle(RedmineManager redmineManager, Issue issue, String value)
              throws Exception {
        issue.setPrivateIssue(true);
      }
    });

    for (Handler handler : handlersList) {
      UpdateIssueCommand.handlers.put(handler.getName(), handler);
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    Integer id = ((NumberArgument) getArguments()[0]).getValue();

    processIssue(id);
  }

  private void processIssue(Integer id) throws Exception {
    IssueManager issueManager = redmineManager.getIssueManager();
    Issue issue = issueManager.getIssueById(id);

    boolean atLeastOneOptionSet = false;
    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      atLeastOneOptionSet = true;

      handlers.get(option.getName())
          .handle(redmineManager, issue, option.buildValue());
    }

    if (!atLeastOneOptionSet) {
      throw new Exception(String.format(NO_OPTION_SET_MESSAGE, getName()));
    }

    issueManager.update(issue);

    println(ISSUE_UPDATE_SUCCESS_MESSAGE, issue.getId());
  }

  private static abstract class Handler {
    public abstract String getName();

    public abstract void handle(RedmineManager redmineManager, Issue issue,
        String value) throws Exception;
  }

}
