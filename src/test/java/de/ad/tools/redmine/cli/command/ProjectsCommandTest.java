package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import de.ad.tools.redmine.cli.Configuration;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.*;

public class ProjectsCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private RedmineManager redmineManager;
  private ProjectManager projectManager;

  private ProjectsCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    out = mock(PrintStream.class);

    redmineManager = mock(RedmineManager.class);
    projectManager = mock(ProjectManager.class);

    command = new ProjectsCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[0];

    List<Project> projects = createDummyProjects(2);

    when(redmineManager.getProjectManager()).thenReturn(projectManager);
    when(projectManager.getProjects()).thenReturn(projects);

    command.process(arguments);

    verify(out).println("Project 1  project-1  ");
    verify(out).println("Project 2  project-2  ");
  }

  private List<Project> createDummyProjects(int count) {
    List<Project> projects = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      String name = String.format("Project %d", i+1);
      String identifier = String.format("project-%d", i+1);

      projects.add(ProjectFactory.create(name, identifier));
    }

    return projects;
  }
}
