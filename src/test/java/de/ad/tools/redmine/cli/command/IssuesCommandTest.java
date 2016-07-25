package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.MembershipManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IssuesCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;
  private IssueManager issueManager;
  private MembershipManager membershipManager;
  private ProjectManager projectManager;

  private IssuesCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);

    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);
    membershipManager = mock(MembershipManager.class);
    projectManager = mock(ProjectManager.class);

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);
    when(redmineManager.getProjectManager()).thenReturn(projectManager);

    command = new IssuesCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    List<Issue> issues = createDummyIssues(2);

    when(issueManager.getIssues(any(Map.class))).thenReturn(issues);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/IssuesCommandOutput.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testWithProjectOption() throws Exception {
    List<Project> projects = createDummyProjects();
    when(projectManager.getProjects()).thenReturn(projects);

    String[] arguments = new String[] { "--project=Project 2" };

    command.process(arguments);

    Map<String, String> parameters = new HashMap<>();
    parameters.put("project_id", "2");

    verify(issueManager).getIssues(parameters);
  }

  @Test
  public void testWithPriorityOption() throws Exception {
    List<IssuePriority> priorities = createDummyPriorities();
    when(issueManager.getIssuePriorities()).thenReturn(priorities);

    String[] arguments = new String[] { "--priority=High" };

    command.process(arguments);

    Map<String, String> parameters = new HashMap<>();
    parameters.put("priority_id", "2");

    verify(issueManager).getIssues(parameters);
  }

  @Test
  public void testWithAssigneeOption() throws Exception {
    String[] arguments = new String[] { "--assignee=me" };

    command.process(arguments);

    Map<String, String> parameters = new HashMap<>();
    parameters.put("assigned_to_id", "me");

    verify(issueManager).getIssues(parameters);
  }

  @Test
  public void testWithInvalidAssigneeOption() throws Exception {
    String[] arguments = new String[] { "--assignee=invalid" };

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(IssuesCommand.INVALID_ASSIGNEE_MESSAGE, "invalid"));

    command.process(arguments);
  }

  @Test
  public void testWithStatusOption() throws Exception {
    List<IssueStatus> statuses = createDummyStatuses();
    when(issueManager.getStatuses()).thenReturn(statuses);

    String[] arguments = new String[] { "--status=Closed" };

    command.process(arguments);

    Map<String, String> parameters = new HashMap<>();
    parameters.put("status_id", "2");

    verify(issueManager).getIssues(parameters);
  }

  @Test
  public void testWithTrackerOption() throws Exception {
    List<Tracker> trackers = createDummyTrackers();
    when(issueManager.getTrackers()).thenReturn(trackers);

    String[] arguments = new String[] { "--tracker=Feature" };

    command.process(arguments);

    Map<String, String> parameters = new HashMap<>();
    parameters.put("tracker_id", "2");

    verify(issueManager).getIssues(parameters);
  }

  @After
  public void tearDown() throws Exception {
    Arrays.stream(command.getOptions()).forEach(o -> o.setValue(null));
  }

  private List<Issue> createDummyIssues(int count) {
    List<Issue> issues = new ArrayList<>();

    Tracker tracker = mock(Tracker.class);
    when(tracker.getName()).thenReturn("Bug");
    User user = mock(User.class);
    when(user.getFullName()).thenReturn("John Doe");

    Date updatedOn = Date.from(LocalDateTime.now().minusHours(1).atZone(
        ZoneId.systemDefault()).toInstant());

    for (int i = 0; i < count; i++) {
      Issue issue = mock(Issue.class);
      when(issue.getId()).thenReturn(i + 1);

      when(issue.getSubject()).thenReturn("Issue " + (i + 1));
      when(issue.getTracker()).thenReturn(tracker);
      when(issue.getStatusName()).thenReturn("New");
      when(issue.getPriorityText()).thenReturn("Normal");
      if (i % 2 == 0) {
        when(issue.getAssignee()).thenReturn(user);
      } else {
        when(issue.getAssignee()).thenReturn(null);
      }
      when(issue.getUpdatedOn()).thenReturn(updatedOn);

      issues.add(issue);
    }

    Collections.reverse(issues);

    return issues;
  }

  private List<IssuePriority> createDummyPriorities() {
    IssuePriority normal = mock(IssuePriority.class);
    when(normal.getName()).thenReturn("Normal");
    when(normal.getId()).thenReturn(1);

    IssuePriority high = mock(IssuePriority.class);
    when(high.getName()).thenReturn("High");
    when(high.getId()).thenReturn(2);

    return Arrays.asList(normal, high);
  }

  private List<Membership> createDummyMemberships() {
    User user1 = mock(User.class);
    when(user1.getFullName()).thenReturn("User Name 1");
    User user2 = mock(User.class);
    when(user2.getFullName()).thenReturn("User Name 2");

    Membership membership1 = mock(Membership.class);
    when(membership1.getUser()).thenReturn(user1);

    Membership membership2 = mock(Membership.class);
    when(membership2.getUser()).thenReturn(user2);

    return Arrays.asList(membership1, membership2);
  }

  private List<IssueStatus> createDummyStatuses() {
    IssueStatus newStatus = mock(IssueStatus.class);
    when(newStatus.getName()).thenReturn("New");
    when(newStatus.getId()).thenReturn(1);
    when(newStatus.isDefaultStatus()).thenReturn(true);
    when(newStatus.isClosed()).thenReturn(false);

    IssueStatus closedStatus = mock(IssueStatus.class);
    when(closedStatus.getName()).thenReturn("Closed");
    when(closedStatus.getId()).thenReturn(2);
    when(closedStatus.isDefaultStatus()).thenReturn(false);
    when(closedStatus.isClosed()).thenReturn(true);

    return Arrays.asList(newStatus, closedStatus);
  }

  private List<Tracker> createDummyTrackers() {
    Tracker bugTracker = mock(Tracker.class);
    when(bugTracker.getName()).thenReturn("Bug");
    when(bugTracker.getId()).thenReturn(1);

    Tracker featureTracker = mock(Tracker.class);
    when(featureTracker.getName()).thenReturn("Feature");
    when(featureTracker.getId()).thenReturn(2);

    return Arrays.asList(bugTracker, featureTracker);
  }

  private List<Project> createDummyProjects() {
    Project project1 = mock(Project.class);
    when(project1.getId()).thenReturn(1);
    when(project1.getName()).thenReturn("Project 1");
    when(project1.getIdentifier()).thenReturn("project-1");

    Project project2 = mock(Project.class);
    when(project2.getId()).thenReturn(2);
    when(project2.getName()).thenReturn("Project 2");
    when(project2.getIdentifier()).thenReturn("project-2");

    return Arrays.asList(project1, project2);
  }
}
