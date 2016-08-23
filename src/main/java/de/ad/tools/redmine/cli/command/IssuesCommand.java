package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.DateUtil;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.PrintUtil;
import de.ad.tools.redmine.cli.util.RedmineUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class IssuesCommand extends RedmineCommand {

  static final String INVALID_PROJECT_MESSAGE = "'%s' is not a valid project.";
  static final String INVALID_PRIORITY_MESSAGE = "'%s' is not a valid priority.";
  static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";
  static final String INVALID_STATUS_MESSAGE = "'%s' is not a valid status.";
  static final String INVALID_TRACKER_MESSAGE = "'%s' is not a valid tracker.";
  static final String INVALID_SORT_MESSAGE = "'%s' is not a valid sort.";
  static final String INVALID_LIMIT_MESSAGE = "'%s' is not a valid limit.";
  static final String INVALID_OFFSET_MESSAGE = "'%s' is not a valid offset.";
  static final String INVALID_PAGE_MESSAGE = "'%s' is not a valid page.";

  private static final String NAME = "issues";
  private static final String DESCRIPTION = "Display issues.";
  private static final Argument[] ARGUMENTS = new Argument[0];
  private static final Option[] OPTIONS = new Option[] {
      new Option("project", "Only display issues for the specified project."),
      new Option("priority", "Only display issues with specified priority."),
      new Option("assignee", "Only display issues for the specified assignee."),
      new Option("status", "Only display issues with the specified status."),
      new Option("tracker", "Only display issues for the specified tracker."),
      new Option("limit", "Set max number of issues."),
      new Option("offset", "Set the offset to start with."),
      new Option("page", "Set the page to show."),
      new Option("sort", "Column to sort with. Append :desc to invert the order."),
      new Option("extra-fields", "extra fields to show on results. Format <name>:<field>[,<name>:<field>]+"),
      Option.buildOptionWithoutValue("time", "Show time entries for issues")
  };

  private static final Map<String, Handler> handlers = new HashMap<>();
  private LinkedHashMap<String, FieldInfo> extraFields = new LinkedHashMap<>();
  private boolean time;

  public IssuesCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out,
        redmineManager);

    ArrayList<Handler> handlers = new ArrayList<>();
    handlers.add(new ProjectHandler());
    handlers.add(new PriorityHandler());
    handlers.add(new AssigneeHandler());
    handlers.add(new StatusHandler());
    handlers.add(new TrackerHandler());
    handlers.add(new SortHandler());
    handlers.add(new Handler() {
      @Override
      public String getName() {
        return "limit";
      }

      @Override
      public void handle(RedmineManager redmineManager, Map<String, String> parameters, String value) throws Exception {
        try {
          Integer.parseInt(value);
          parameters.put("limit", value);
        } catch (Exception e) {
          throw new Exception(String.format(INVALID_LIMIT_MESSAGE, value));
        }
      }
    });
    handlers.add(new Handler() {
      @Override
      public String getName() {
        return "offset";
      }

      @Override
      public void handle(RedmineManager redmineManager, Map<String, String> parameters, String value) throws Exception {
        try {
          Integer.parseInt(value);
          parameters.put("offset", value);
        } catch (Exception e) {
          throw new Exception(String.format(INVALID_OFFSET_MESSAGE, value));
        }
      }
    });
    handlers.add(new Handler() {
      @Override
      public String getName() {
        return "page";
      }

      @Override
      public void handle(RedmineManager redmineManager, Map<String, String> parameters, String value) throws Exception {
        try {
          Integer.parseInt(value);
          parameters.put("page", value);
        } catch (Exception e) {
          throw new Exception(String.format(INVALID_PAGE_MESSAGE, value));
        }
      }
    });
    handlers.add(new Handler() {
      @Override
      public String getName() {
        return "extra-fields";
      }

      @Override
      public void handle(RedmineManager redmineManager, Map<String, String> parameters, String value) throws Exception {
        try {
          String[] fields = value.split("\\s*,\\s*");
          for (String fieldInfoStr : fields) {
            String[] split = fieldInfoStr.split(":");
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setTitle(split[0]);
            if (split.length > 1) {
              fieldInfo.setField(split[1]);
            } else {
              fieldInfo.setField(fieldInfo.getTitle());
            }

            if (split.length > 2) {
              fieldInfo.setFormat(split[2]);
            }
            extraFields.put(fieldInfo.getTitle(), fieldInfo);
          }
        } catch (Exception e) {
          throw new Exception(String.format(INVALID_LIMIT_MESSAGE, value));
        }
      }
    });
    handlers.add(new Handler() {
      @Override
      public String getName() {
        return "time";
      }

      @Override
      public void handle(RedmineManager redmineManager, Map<String, String> parameters, String value) throws Exception {
        time = true;
      }
    });

    for (Handler handler : handlers) {
      IssuesCommand.handlers.put(handler.getName(), handler);
    }
  }

  class FieldInfo {
    String title;
    String field;
    String format;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getFormat() {
      return format;
    }

    public void setFormat(String format) {
      this.format = format;
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    Map<String, String> parameters = buildParameterMapFromOptions();

    IssueManager issueManager = redmineManager.getIssueManager();
    ResultsWrapper<Issue> issuesResultsWrapper = issueManager.getIssues(parameters);
    List<Issue> issues = issuesResultsWrapper.getResults();

    final String[][] issueTable = new String[issues.size()][6];
    Vector<String> columns = new Vector<>();
    columns.add("ID");
    columns.add("Project");
    columns.add("Tracker");
    columns.add("Status");
    columns.add("Priority");
    columns.add("Assignee");
    columns.add("Updated");
    columns.addAll(extraFields.keySet());
    columns.add("Subject");

    HashMap<Integer, String> subtotalMap = new HashMap<>();
    int i = 0;
    for (Issue issue : issues) {
      String subtotal = "";
      issueTable[i++] = buildRow(issue);
      if (time) {
        List<TimeEntry> timeEntries = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(issue.getId());
        subtotal = buildTimeEntries(timeEntries, false);
      }
      if (!subtotal.isEmpty()) {
        subtotalMap.put(i + 1, subtotal);
      }
    }

    printTable(columns.toArray(new String[]{}), issueTable, subtotalMap);
    printResults(issuesResultsWrapper);
  }

  public String buildTimeEntries(List<TimeEntry> timeEntries, boolean decorations) {
    String timeEntriesStr = "";
    if (decorations) {
      timeEntriesStr += "Time Entries"+"\n";
    }
    Iterator<TimeEntry> it = timeEntries.iterator();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    while (it.hasNext()) {
      TimeEntry timeEntry = it.next();
      String comment = timeEntry.getComment();
      if (!StringUtils.isBlank(comment)) {
        comment = ": "+comment;
      }
      timeEntriesStr +=" -- TIME -- "
              +"User \"" + timeEntry.getUserName() + "\" spent " + timeEntry.getHours()
              + " hours doing " + timeEntry.getActivityName()+" on "+sdf.format(timeEntry.getSpentOn())+ comment;
      if (it.hasNext()) {
        timeEntriesStr +="\n";
      }
    }
//    if (!timeEntries.isEmpty()) {
//      timeEntriesStr +="\n";
//    }

    if (decorations) {
      timeEntriesStr +="\n";
    }
    return timeEntriesStr;
  }


  private Map<String, String> buildParameterMapFromOptions() throws Exception {
    HashMap<String, String>parameters = new HashMapDuplicates();

    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      handlers.get(option.getName())
          .handle(redmineManager, parameters, option.buildValue());
    }
    return parameters;
  }

  private String[] buildRow(Issue issue) {

    Vector<String> columnData = new Vector<>();
    columnData.add(""+issue.getId());
    columnData.add(issue.getProjectName());
    columnData.add(issue.getTracker().getName());
    columnData.add(issue.getStatusName());
    columnData.add(issue.getPriorityText());
    columnData.add(getAssigneeName(issue));
    columnData.add(getTimeDifferenceAsText(issue));
    if (!extraFields.isEmpty()) {
      for (String column : extraFields.keySet()) {
        FieldInfo fieldInfo = extraFields.get(column);
        String field = fieldInfo.getField();
        try {
          Method method = issue.getClass().getMethod("get" + WordUtils.capitalizeFully(field, new char[]{'_'}).replaceAll("_", ""));
          Object data = method.invoke(issue);
          String format = fieldInfo.getFormat();
          if (data == null) {
            columnData.add("");
          } else {
            if (format == null) {
              columnData.add(data.toString());
            } else {
              if (data instanceof Date) {
                columnData.add(new SimpleDateFormat(fieldInfo.getFormat()).format(data));
              } else {
                columnData.add(data.toString());
              }
            }
          }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    columnData.add(issue.getSubject());

    return columnData.toArray(new String[]{});
  }

  private String getTimeDifferenceAsText(Issue issue) {
    return String.format("%s ago", DateUtil.getTimeDifferenceAsText(issue.getUpdatedOn()));
  }

  private String getAssigneeName(Issue issue) {
    return issue.getAssigneeName() != null ?
        issue.getAssigneeName() :
        "(not assigned)";
  }

  private static abstract class Handler {
    public abstract String getName();

    public abstract void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value) throws Exception;
  }

  private static class ProjectHandler extends Handler {

    @Override public String getName() {
      return "project";
    }

    @Override public void handle(RedmineManager redmineManager,
      Map<String, String> parameters, String value) throws Exception {

      if ("mine".equalsIgnoreCase(value)) {
        HashMapDuplicates.addFormParameterEqual(parameters, "project_id", value);
      } else {
        Optional<Project> project = RedmineUtil.resolveProjectByName(redmineManager, value);

        project.ifPresent(p -> HashMapDuplicates.addFormParameterEqual(parameters, "project_id", String.valueOf(p.getId())));
        project.orElseThrow(() -> new Exception(String.format(INVALID_PROJECT_MESSAGE, value)));
      }
    }
  }

  private static class PriorityHandler extends Handler {

    @Override public String getName() {
      return "priority";
    }

    @Override public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value) throws Exception {
      Optional<IssuePriority> priority =
          RedmineUtil.resolvePriorityByName(redmineManager, value);

      priority.ifPresent(p -> parameters.put("priority_id", String.valueOf(
          p.getId())));
      priority.orElseThrow(
          () -> new Exception(String.format(INVALID_PRIORITY_MESSAGE, value)));
    }
  }

  private static class AssigneeHandler extends Handler {

    @Override public String getName() {
      return "assignee";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      if ("me".equalsIgnoreCase(value) || value.matches("[0-9]+")) {
        HashMapDuplicates.addFormParameterEqual(parameters, "assigned_to_id", value);
      } else if ("none".equalsIgnoreCase(value) || "nobody".equalsIgnoreCase(value)) {
        HashMapDuplicates.addFormParameter(parameters, "assigned_to_id", null, "!*");
      } else {
        throw new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value));
      }
    }
  }

  private static class StatusHandler extends Handler {

    @Override public String getName() {
      return "status";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      if (value.contains(",")) {
        String[] split = value.trim().split(",");
        for (int i = 0; i < split.length; i++) {
          String s = split[i];
          Optional<IssueStatus> status = RedmineUtil.resolveStatusByName(redmineManager, s);
          if (status.isPresent()) {
            String statusId = String.valueOf(status.get().getId());
            HashMapDuplicates.addFormParameterEqual(parameters, "status_id", statusId);
          }
        }
      } else if ("open".equalsIgnoreCase(value) || "close".equalsIgnoreCase(value)) {
        parameters.put("status_id", value);
      } else {
        Optional<IssueStatus> status = RedmineUtil.resolveStatusByName(redmineManager, value);
        if (status.isPresent()) {
          String statusId = String.valueOf(status.get().getId());
          HashMapDuplicates.addFormParameterEqual(parameters, "status_id", statusId);
        } else {
          throw new Exception(String.format(INVALID_STATUS_MESSAGE, value));
        }
      }
    }
  }

  private static class TrackerHandler extends Handler {

    @Override public String getName() {
      return "tracker";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      Optional<Tracker> tracker =
          RedmineUtil.resolveTrackerByName(redmineManager, value);

      tracker.ifPresent(t -> HashMapDuplicates.addFormParameterEqual(parameters, "tracker_id", String.valueOf(
          t.getId())));
      tracker.orElseThrow(() ->
          new Exception(String.format(INVALID_TRACKER_MESSAGE, value)));
    }
  }

  private static class SortHandler extends Handler {

    @Override public String getName() {
      return "sort";
    }

    @Override
    public void handle(RedmineManager redmineManager,
        Map<String, String> parameters, String value)
        throws Exception {
      if (!StringUtils.isBlank(value)) {
        //TODO: Parse sort
        parameters.put("sort", value);
      } else {
        throw new Exception(String.format(INVALID_SORT_MESSAGE, value));
      }
    }
  }

}
