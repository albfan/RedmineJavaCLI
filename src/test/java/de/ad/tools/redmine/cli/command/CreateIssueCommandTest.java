package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.MembershipManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateIssueCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineManager redmineManager;

  private ProjectManager projectManager;
  private IssueManager issueManager;

  private CreateIssueCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(true);

    out = mock(PrintStream.class);

    redmineManager = mock(RedmineManager.class);
    projectManager = mock(ProjectManager.class);
    issueManager = mock(IssueManager.class);

    Project project = mock(Project.class);
    when(project.getId()).thenReturn(1);

    when(projectManager.getProjectByKey("project-1")).thenReturn(project);

    when(redmineManager.getProjectManager()).thenReturn(projectManager);
    when(redmineManager.getIssueManager()).thenReturn(issueManager);

    command = new CreateIssueCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCreate() throws Exception {
    String[] arguments = new String[] { "project-1", "This is the subject." };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    when(issueManager.createIssue(any(Issue.class))).thenReturn(issue);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithInvalidProjectKey() throws Exception {
    String[] arguments = new String[] { "invalid", "This is the subject." };

    exception.expect(Exception.class);

    command.process(arguments);
  }

  @Test
  public void testCreateWithDescription() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--description=A new description" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    issueToCreate.setDescription("A new description");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithPriority() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.", "--priority=High" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    List<IssuePriority> priorities = createDummyPriorities();
    when(issueManager.getIssuePriorities()).thenReturn(priorities);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    issueToCreate.setPriorityId(2);
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithInvalidPriority() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--priority=Invalid" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    List<IssuePriority> priorities = createDummyPriorities();
    when(issueManager.getIssuePriorities()).thenReturn(priorities);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    issueToCreate.setPriorityId(2);
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(CreateIssueCommand.INVALID_PRIORITY_MESSAGE,
            "Invalid"));

    command.process(arguments);
  }

  @Test
  public void testCreateWithAssignee() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--assignee=User Name 1" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    Project project1 = mock(Project.class);
    when(project1.getId()).thenReturn(1);

    issueToCreate.setProject(project1);
    
    List<Membership> memberships = createDummyMemberships();
    MembershipManager membershipManager = mock(MembershipManager.class);
    when(membershipManager.getMemberships(1)).thenReturn(memberships);

    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithInvalidAssignee() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--assignee=Invalid" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    Project project1 = mock(Project.class);
    when(project1.getId()).thenReturn(1);

    issueToCreate.setProject(project1);
    
    List<Membership> memberships = createDummyMemberships(); 
    MembershipManager membershipManager = mock(MembershipManager.class);
    when(membershipManager.getMemberships(1)).thenReturn(memberships);

    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(CreateIssueCommand.INVALID_ASSIGNEE_MESSAGE,
            "Invalid"));

    command.process(arguments);
  }

  @Test
  public void testCreateWithStatus() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--status=Closed" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    List<IssueStatus> statuses = createDummyStatuses();
    when(issueManager.getStatuses()).thenReturn(statuses);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithInvalidStatus() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--status=Invalid" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    List<IssueStatus> statuses = createDummyStatuses();
    when(issueManager.getStatuses()).thenReturn(statuses);

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(CreateIssueCommand.INVALID_STATUS_MESSAGE,
            "Invalid"));

    command.process(arguments);
  }

  @Test
  public void testCreateTracker() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--tracker=Feature" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    List<Tracker> trackers = createDummyTrackers();
    when(issueManager.getTrackers()).thenReturn(trackers);

    command.process(arguments);

    String message = String.format(CreateIssueCommand.SUCCESS_MESSAGE, 1);
    verify(out).println(message);
  }

  @Test
  public void testCreateWithInvalidTracker() throws Exception {
    String[] arguments =
        new String[] { "project-1", "This is the subject.",
            "--tracker=Invalid" };

    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(1);

    Issue issueToCreate = IssueFactory.create(1, "This is the subject.");
    when(issueManager.createIssue(issueToCreate)).thenReturn(issue);

    List<Tracker> trackers = createDummyTrackers();
    when(issueManager.getTrackers()).thenReturn(trackers);

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(CreateIssueCommand.INVALID_TRACKER_MESSAGE,
            "Invalid"));

    command.process(arguments);
  }

  @After
  public void tearDown() throws Exception {
    Arrays.stream(command.getOptions()).forEach(o -> o.setValue(null));
  }

  private Issue createMockIssue(int id) {
    Tracker tracker = mock(Tracker.class);
    when(tracker.getName()).thenReturn("Bug");

    Date createdOn = Date.from(
        LocalDateTime.now()
            .minusHours(1)
            .atZone(ZoneId.systemDefault())
            .toInstant());
    Date updatedOn = Date.from(
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

    User author = mock(User.class);
    when(author.getFullName()).thenReturn("John Doe");

    Issue issue = mock(Issue.class);
    when(issue.getTracker()).thenReturn(tracker);
    when(issue.getId()).thenReturn(id);
    when(issue.getSubject()).thenReturn("Subject of #" + id);
    when(issue.getCreatedOn()).thenReturn(createdOn);
    when(issue.getUpdatedOn()).thenReturn(updatedOn);
    when(issue.getAuthor()).thenReturn(author);
    when(issue.getAssignee()).thenReturn(author);
    when(issue.getDescription()).thenReturn("Description of #" + id);

    return issue;
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
}
