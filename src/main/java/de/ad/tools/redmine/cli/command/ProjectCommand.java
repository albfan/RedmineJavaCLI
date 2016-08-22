package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.*;
import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProjectCommand extends RedmineCommand {

  private static final String NAME = "project";
  private static final String DESCRIPTION = "Display project details.";
  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new TextArgument("key", "The key of the project to display.",
              false) };

  public ProjectCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out,
        redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    ProjectManager projectManager = redmineManager.getProjectManager();

    String key = ((TextArgument)getArguments()[0]).getValue();
    Project project = projectManager.getProjectByKey(key);

    println(project.getName());
    println();
    println(project.getDescription() != null &&
        project.getDescription().length() > 0
        ? project.getDescription() : "(Description not set)");
    println();

    printMembershipDetails(key);
  }

  private void printMembershipDetails(String projectKey)
      throws RedmineException {
    MembershipManager membershipManager =
        redmineManager.getMembershipManager();
    List<Membership> memberships =
        membershipManager.getMemberships(projectKey);
    
    LinkedHashMap<Role, List<User>> rolesAndMembers =
        new LinkedHashMap<Role, List<User>>();
    memberships.forEach(
        membership -> membership.getRoles().forEach(
            role -> addRoleForMembership(role, membership, rolesAndMembers)));

    String[][] membershipDetails = new String[rolesAndMembers.size()][2];
    int i = 0;
    for (Map.Entry<Role, List<User>> roleAndMembers : rolesAndMembers
        .entrySet()) {
      String members = getMemberListAsString(roleAndMembers);
      membershipDetails[i++] =
          new String[] { roleAndMembers.getKey().getName() + ":",
              members.toString() };
    }

    printHeading("Members");
    printTable(membershipDetails);
    println();
  }

  private void addRoleForMembership(Role role, Membership membership,
      LinkedHashMap<Role, List<User>> rolesAndMembers) {
    User user = new User(membership.getUserId());
    user.setFirstName(membership.getUserName());
    if (rolesAndMembers.containsKey(role)) {
      rolesAndMembers.get(role).add(user);
    } else {
      List<User> members = new ArrayList<User>();
      members.add(user);

      rolesAndMembers.put(role, members);
    }
  }

  private String getMemberListAsString(
      Map.Entry<Role, List<User>> roleAndMembers) {
    StringBuilder members = new StringBuilder();
    for (User user : roleAndMembers.getValue()) {
      if (members.length() > 0) {
        members.append(", ");
      }
      members.append(user.getFullName());
    }
    return members.toString();
  }
}
