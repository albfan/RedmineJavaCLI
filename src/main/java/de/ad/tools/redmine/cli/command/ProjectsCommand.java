package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;
import java.util.List;

public class ProjectsCommand extends RedmineCommand {

  private static final String NAME = "projects";
  private static final String DESCRIPTION = "Displays your projects.";
  private static final Argument[] ARGUMENTS = new Argument[0];

  public ProjectsCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, ARGUMENTS, configuration, out, redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    ProjectManager projectManager = redmineManager.getProjectManager();

    List<Project> projects = projectManager.getProjects();

    String[] header = new String[]{"Name", "Key"};
    
    String[][] projectTable = new String[projects.size()][2];
    int i = 0;
    for (Project project : projects) {
      projectTable[i++] =
          new String[] { project.getName(), project.getIdentifier() };
    }

    printTable(header, projectTable);
  }
}
