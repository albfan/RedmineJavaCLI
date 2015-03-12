package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.TrackerFactory;
import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IssuesCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineManager redmineManager;
  private IssueManager issueManager;

  private IssuesCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    out = mock(PrintStream.class);

    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);

    command = new IssuesCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    List<Issue> issues = createDummyIssues(2);

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(issueManager.getIssues(any(Map.class))).thenReturn(issues);

    command.process(arguments);

    verify(out).println(
        "ID  Tracker  Status  Priority  Assignee  Updated      Description    ");
    verify(out).println(
        "¯¯  ¯¯¯¯¯¯¯  ¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯      ¯¯¯¯¯¯¯¯¯¯¯    ");
    verify(out).println(
        "#2  Bug      New     Normal    John Doe  an hour ago  Description 2  ");
    verify(out).println(
        "#1  Bug      New     Normal    John Doe  an hour ago  Description 1  ");
  }

  private List<Issue> createDummyIssues(int count) {
    List<Issue> issues = new ArrayList<>();

    Tracker tracker = TrackerFactory.create(0, "Bug");
    User user = UserFactory.create();
    user.setFullName("John Doe");

    Date updatedOn = Date.from(LocalDateTime.now().minusHours(1).atZone(
        ZoneId.systemDefault()).toInstant());

    for (int i = 0; i < count; i++) {
      Issue issue = IssueFactory.create(i + 1);

      issue.setSubject("Issue " + (i + 1));
      issue.setTracker(tracker);
      issue.setStatusName("New");
      issue.setPriorityText("Normal");
      issue.setAssignee(user);
      issue.setUpdatedOn(updatedOn);
      issue.setSubject("Description " + (i + 1));

      issues.add(issue);
    }

    Collections.reverse(issues);

    return issues;
  }
}
