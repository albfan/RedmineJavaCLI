package de.ad.tools.redmine.cli.util;

import com.taskadapter.redmineapi.NotAuthorizedException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class RedmineUtil {
  
  private RedmineUtil(){}
  
  public static Optional<IssuePriority> resolvePriorityByName(
      RedmineManager redmineManager, String name) throws RedmineException {
    List<IssuePriority> priorities =
        redmineManager.getIssueManager().getIssuePriorities();

    return priorities.stream()
        .filter(p -> name.equals(p.getName()))
        .findFirst();
  }

  public static Optional<Membership> resolveMembershipByName(
      RedmineManager redmineManager, Integer projectId, String name)
      throws RedmineException {
    List<Membership> memberships =
        redmineManager.getMembershipManager()
            .getMemberships(projectId);

    return memberships.stream()
        .filter(m -> name.equals(m.getUserName()))
        .findFirst();
  }

  public static Optional<IssueStatus> resolveStatusByName(
      RedmineManager redmineManager, String name) throws RedmineException {
    List<IssueStatus> statuses =
        redmineManager.getIssueManager().getStatuses();

    return
        statuses.stream().filter(s -> name.equals(s.getName())).findFirst();
  }

  public static Optional<Tracker> resolveTrackerByName(
      RedmineManager redmineManager, String name) throws RedmineException {
    List<Tracker> trackers = redmineManager.getIssueManager().getTrackers();

    return trackers.stream()
        .filter(t -> name.equals(t.getName())).findFirst();
  }

  public static Optional<Project> resolveProjectByName(
      RedmineManager redmineManager,
      String name) throws RedmineException {
    List<Project> projects = redmineManager.getProjectManager().getProjects();

    return projects.stream().filter(p -> name.equals(p.getName())).findFirst();
  }

  public static Optional<TimeEntryActivity> resolveActivityByName(
          RedmineManager redmineManager,
          String name) throws RedmineException {
    List<TimeEntryActivity> timeEntryActivities = redmineManager.getTimeEntryManager().getTimeEntryActivities();

    return timeEntryActivities.stream().filter(p -> name.equals(p.getName())).findFirst();
  }
  
  public static Optional<User> resolveUserByName(
          RedmineManager redmineManager,
          String name) throws RedmineException {
    if ("me".equals(name)) {
      User user = redmineManager.getUserManager().getCurrentUser();
      return Optional.of(user);
    } else {
      HashMap<String, String> parameters = new HashMap<>();
      parameters.put("name", name);
      List<User> users = redmineManager.getUserManager().getUsers(parameters).getResults();
      return users.stream().findFirst();
    }
  }
}
