package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.MembershipManager;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Membership;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.Role;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;
  private ProjectManager projectManager;
  private MembershipManager membershipManager;

  private ProjectCommand command;
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);

    redmineManager = mock(RedmineManager.class);
    projectManager = mock(ProjectManager.class);
    membershipManager = mock(MembershipManager.class);

    when(redmineManager.getProjectManager()).thenReturn(projectManager);
    when(redmineManager.getMembershipManager()).thenReturn(membershipManager);

    command = new ProjectCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[] { "key" };

    Project project = createMockProject("key");
    when(projectManager.getProjectByKey("key")).thenReturn(project);

    command.process(arguments);

    //Maybe it's faster to use byte[]
    //However, String provides a better comparison in case of differences
    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/ProjectCommandOutput.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  private Project createMockProject(String key) throws RedmineException {
    User user1 = mock(User.class);
    when(user1.getFullName()).thenReturn("Jane Doe");

    User user2 = mock(User.class);
    when(user2.getFullName()).thenReturn("John Doe");

    User user3 = mock(User.class);
    when(user3.getFullName()).thenReturn("Jason Doe");

    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project 1");
    when(project.getDescription()).thenReturn("Description");

    Role managerRole = mock(Role.class);
    when(managerRole.getName()).thenReturn("Manager");

    Role developerRole = mock(Role.class);
    when(developerRole.getName()).thenReturn("Developer");

    Membership manager = mock(Membership.class);
    Membership developer1 = mock(Membership.class);
    Membership developer2 = mock(Membership.class);
    when(manager.getRoles()).thenReturn(Arrays.asList(managerRole));
    when(manager.getUser()).thenReturn(user1);
    when(developer1.getRoles()).thenReturn(Arrays.asList(developerRole));
    when(developer1.getUser()).thenReturn(user2);
    when(developer2.getRoles()).thenReturn(Arrays.asList(developerRole));
    when(developer2.getUser()).thenReturn(user3);

    List<Membership> memberships =
        Arrays.asList(manager, developer1, developer2);

    when(membershipManager.getMemberships(key)).thenReturn(memberships);

    return project;
  }
}
