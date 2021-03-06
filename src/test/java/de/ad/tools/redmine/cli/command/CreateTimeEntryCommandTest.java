package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.*;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CreateTimeEntryCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;
  private IssueManager issueManager;
  private MembershipManager membershipManager;
  private ProjectManager projectManager;
  private TimeEntryManager timeEntryManager;
  private UserManager userManager;

  private CreateTimeEntryCommand command;

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
    
    buildUserManager();
    buildTimeEntriesManager();

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);
    when(redmineManager.getProjectManager()).thenReturn(projectManager);


    command = new CreateTimeEntryCommand(configuration, out, redmineManager);
  }

  private void buildUserManager() throws RedmineException {
    userManager = mock(UserManager.class);
    User user = mock(User.class);
    when(user.getFirstName()).thenReturn("John");
    when(user.getLastName()).thenReturn("Doe");
    when(user.getId()).thenReturn(1);
    when(userManager.getCurrentUser()).thenReturn(user);
    when(redmineManager.getUserManager()).thenReturn(userManager);
  }

  private void buildTimeEntriesManager() throws RedmineException {
    timeEntryManager = mock(TimeEntryManager.class);
    ArrayList<TimeEntryActivity> timeEntryActivities = new ArrayList<>();
    TimeEntryActivity timeEntryActivityAnalysis = mock(TimeEntryActivity.class);
    when(timeEntryActivityAnalysis.getId()).thenReturn(1);
    when(timeEntryActivityAnalysis.getName()).thenReturn("Analysis");
    TimeEntryActivity timeEntryActivityDevelop = mock(TimeEntryActivity.class);
    when(timeEntryActivityDevelop.getId()).thenReturn(2);
    when(timeEntryActivityDevelop.getName()).thenReturn("Develop");
    when(timeEntryActivityDevelop.isDefault()).thenReturn(true);
    timeEntryActivities.add(timeEntryActivityAnalysis);
    timeEntryActivities.add(timeEntryActivityDevelop);

    when(timeEntryManager.getTimeEntryActivities()).thenReturn(timeEntryActivities);
    TimeEntry timeEntry = mock(TimeEntry.class);
    when(timeEntry.getId()).thenReturn(1);
    when(timeEntryManager.createTimeEntry(any(TimeEntry.class))).thenReturn(timeEntry);
    
    when(redmineManager.getTimeEntryManager()).thenReturn(timeEntryManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = {"1", "1"};

    ResultsWrapper<Issue> dummyIssuesResultWrapper = createDummyIssuesResultWrapper(2);
    when(issueManager.getIssues(any(Map.class))).thenReturn(dummyIssuesResultWrapper);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/TimeEntryCommandOutput.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testWithActivityOption() throws Exception {
    List<IssuePriority> priorities = createDummyPriorities();
    when(issueManager.getIssuePriorities()).thenReturn(priorities);
    Map<String, String> parameters = new HashMapDuplicates();
    parameters.put("priority_id", "2");

    ResultsWrapper<Issue> resultsWrapper = createDummyIssuesResultWrapper(2);
    when(issueManager.getIssues(parameters)).thenReturn(resultsWrapper);

    String[] arguments = {"1", "1", "--activity=Develop" };

    command.process(arguments);
  }

  @Test
  public void testWithUserOption() throws Exception {
    String[] arguments = {"1", "1", "--user=me" };
    ResultsWrapper<Issue> dummyIssuesResultWrapper = createDummyIssuesResultWrapper(2);
    when(issueManager.getIssues(any(Map.class))).thenReturn(dummyIssuesResultWrapper);
    command.process(arguments);
  }

  @After
  public void tearDown() throws Exception {
    Arrays.stream(command.getOptions()).forEach(o -> o.setValue(null));
  }

  public ResultsWrapper<Issue> createDummyIssuesResultWrapper(int count) {

    List<Issue> dummyIssues = createDummyIssues(count);
    ResultsWrapper<Issue> issueResultsWrapper = new ResultsWrapper<>(25,2,0,"", dummyIssues);
    return issueResultsWrapper;
  }

  private List<Issue> createDummyIssues(int count) {
    List<Issue> issues = new ArrayList<>();

    Tracker tracker = mock(Tracker.class);
    when(tracker.getName()).thenReturn("Bug");
    User user = mock(User.class);
    when(user.getFullName()).thenReturn("John Doe");

    Date updatedOn = Date.from(LocalDateTime.now().minusHours(1)
        .atZone(ZoneId.systemDefault()).toInstant());

    Project project = mock(Project.class);
    when(project.getName()).thenReturn("project");

    ArrayList<Journal> journals = new ArrayList<>();
    Journal journal = Mockito.mock(Journal.class);
    when(journal.getNotes()).thenReturn("a comment");
    journals.add(journal);

    for (int i = 0; i < count; i++) {
      Issue issue = createMockIssue(tracker, updatedOn, project, journals, i);
      issues.add(issue);
    }

    Collections.reverse(issues);

    return issues;
  }

  private Issue createMockIssue(Tracker tracker, Date updatedOn, Project project, ArrayList<Journal> journals, int i) {
    Issue issue = mock(Issue.class);
    when(issue.getId()).thenReturn(i + 1);

    when(issue.getSubject()).thenReturn("Issue " + (i + 1));
    Integer id = project.getId();
    when(issue.getProjectId()).thenReturn(id);
    String name = project.getName();
    when(issue.getProjectName()).thenReturn(name);
    when(issue.getTracker()).thenReturn(tracker);
    when(issue.getStatusName()).thenReturn("New");
    when(issue.getPriorityText()).thenReturn("Normal");
    if (i % 2 == 0) {
      when(issue.getAssigneeId()).thenReturn(1);
      when(issue.getAssigneeName()).thenReturn("John");
    } else {
      when(issue.getAssigneeId()).thenReturn(null);
      when(issue.getAssigneeName()).thenReturn(null);
    }
    when(issue.getUpdatedOn()).thenReturn(updatedOn);

    when(issue.getJournals()).thenReturn(journals);
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
    when(membership1.getUserId()).thenReturn(user1.getId());
    when(membership1.getUserName()).thenReturn(user1.getFirstName());

    Membership membership2 = mock(Membership.class);
    when(membership2.getUserId()).thenReturn(user2.getId());
    when(membership2.getUserName()).thenReturn(user2.getFirstName());

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
