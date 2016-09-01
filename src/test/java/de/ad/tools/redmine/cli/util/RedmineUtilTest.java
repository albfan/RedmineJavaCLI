package de.ad.tools.redmine.cli.util;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.MembershipManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedmineUtilTest {
  private RedmineManager redmineManager;
  private IssueManager issueManager;
  private MembershipManager membershipManager;
  private ProjectManager projectManager;

  @Before
  public void setUp() throws Exception {
    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);
    membershipManager = mock(MembershipManager.class);
    projectManager = mock(ProjectManager.class);

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);
    when(redmineManager.getProjectManager()).thenReturn(projectManager);
  }

  @Test
  public void testResolvePriority() throws Exception {
    List<IssuePriority> priorities = createDummyPriorities();
    when(issueManager.getIssuePriorities()).thenReturn(priorities);

    IssuePriority expected = priorities.get(1);
    Optional<IssuePriority> actual = RedmineUtil.resolvePriorityByName(
        redmineManager, "High");

    assertThat(actual.isPresent()).isTrue();
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  public void testResolveMembership() throws Exception {
    List<Membership> memberships = createDummyMemberships();
    when(membershipManager.getMemberships(any(Integer.class))).thenReturn(
        memberships);

    Membership expected = memberships.get(1);
    Optional<Membership> actual = RedmineUtil.resolveMembershipByName(
        redmineManager, 1, "User2");

    assertThat(actual.isPresent()).isTrue();
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  public void testResolveStatus() throws Exception {
    List<IssueStatus> statuses = createDummyStatuses();
    when(issueManager.getStatuses()).thenReturn(statuses);

    IssueStatus expected = statuses.get(1);
    Optional<IssueStatus> actual =
        RedmineUtil.resolveStatusByName(redmineManager, "Closed");

    assertThat(actual.isPresent()).isTrue();
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  public void testResolveTracker() throws Exception {
    List<Tracker> trackers = createDummyTrackers();
    when(issueManager.getTrackers()).thenReturn(trackers);

    Tracker expected = trackers.get(1);
    Optional<Tracker> actual = RedmineUtil.resolveTrackerByName(
        redmineManager, "Feature");

    assertThat(actual.isPresent()).isTrue();
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  public void testResolveProject() throws Exception {
    List<Project> projects = createDummyProjects();
    when(projectManager.getProjects()).thenReturn(projects);

    Project expected = projects.get(1);
    Optional<Project> actual = RedmineUtil.resolveProjectByName(
        redmineManager, "Project 2");

    assertThat(actual.isPresent()).isTrue();
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<?>[] constructors =
        RedmineUtil.class.getDeclaredConstructors();
    constructors[0].setAccessible(true);
    constructors[0].newInstance((Object[]) null);
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
    when(user1.getId()).thenReturn(1);
    when(user1.getFirstName()).thenReturn("User1");
    User user2 = mock(User.class);
    when(user2.getId()).thenReturn(2);
    when(user2.getFirstName()).thenReturn("User2");

    Membership membership1 = mock(Membership.class);
    when(membership1.getUserId()).thenReturn(1);
    when(membership1.getUserName()).thenReturn("User1");

    Membership membership2 = mock(Membership.class);
    when(membership2.getUserId()).thenReturn(2);
    when(membership2.getUserName()).thenReturn("User2");

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
  
  private List<Project> createDummyProjects(){
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
