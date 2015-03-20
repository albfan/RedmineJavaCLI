package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import de.ad.tools.redmine.cli.Configuration;

import de.ad.tools.redmine.cli.util.TemplateUtil;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ProjectsCommand extends RedmineCommand {

  private static final String NAME = "projects";
  private static final String DESCRIPTION = "Display your projects.";
  private static final Argument[] ARGUMENTS = new Argument[0];

  public ProjectsCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out,
        redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    ProjectManager projectManager = redmineManager.getProjectManager();

    List<Project> projects = projectManager.getProjects();

    Function table = o -> TemplateUtil.convertToTable((String) o);

    Map<String, Object> scope = new HashMap<>();
    scope.put("projects", projects);
    scope.put("table", table);

    TemplateUtil.printTemplate(System.out, "Projects.template", scope);
  }
}
