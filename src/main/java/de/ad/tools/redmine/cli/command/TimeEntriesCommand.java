package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.TimeEntryActivity;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.RedmineUtil;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class TimeEntriesCommand extends RedmineCommand {

  static final String INVALID_PROJECT_MESSAGE = "'%s' is not a valid project.";
  static final String INVALID_ACTIVITY_MESSAGE = "'%s' is not a valid activity.";
  static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";

  private static final String NAME = "time-entries";
  private static final String DESCRIPTION = "Show time entries";
  private static final Command.Argument[] ARGUMENTS = new Command.Argument[0];
  private static final Option[] OPTIONS = new Option[]{
      new Option("project", "Only display time entries for the specified project."),
      new Option("activity", "Only display time entries with specified activity."),
      new Option("assignee", "Only display time entries for the specified assignee."),
      new Option("spent-on", "Only display time entries spent on range marked."),
      Option.buildOptionWithoutValue("pretty", "Print pretty time entries."),
      Option.buildOptionWithoutValue("time-difference", "Print time as difference from now.")
  };

  private static final Map<String, IHandler> handlers = new HashMap<>();

  private boolean pretty;
  private boolean timeDifference;
  
  public TimeEntriesCommand(Configuration configuration, PrintStream out, RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out, redmineManager);

        /*
        &c[]=issue
        &c[]=comments
        &c[]=hours
         */
    ArrayList<IHandler> handlersList = new ArrayList<>();
    handlersList.add(new DateHandler("spent-on", "spent_on"));

    handlersList.add(new IHandler() {

      @Override
      public String getName() {
        return "assignee";
      }

      @Override
      public void handle(Map<String, String> parameters, String value)
          throws Exception {
        if ("me".equalsIgnoreCase(value) || value.matches("[0-9]+")) {
          HashMapDuplicates.addFormParameterEqual(parameters, "user_id", value);
        } else {
          throw new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value));
        }
      }
    });
    handlersList.add(new IHandler() {

      @Override
      public String getName() {
        return "pretty";
      }

      @Override
      public void handle(Map<String, String> parameters, String value)
          throws Exception {
        pretty = true;
      }
    });
    handlersList.add(new IHandler() {

      @Override
      public String getName() {
        return "time-difference";
      }

      @Override
      public void handle(Map<String, String> parameters, String value)
          throws Exception {
        timeDifference = true;
      }
    });
    handlersList.add(new IHandler() {

      @Override
      public String getName() {
        return "project";
      }

      @Override
      public void handle(Map<String, String> parameters, String value)
          throws Exception {
        Optional<Project> project = RedmineUtil.resolveProjectByName(redmineManager, value);
        project.ifPresent(p -> HashMapDuplicates.addFormParameterEqual(parameters, "project", String.valueOf(p.getId())));
        project.orElseThrow(() -> new Exception(String.format(INVALID_PROJECT_MESSAGE, value)));
      }
    });
    handlersList.add(new IHandler() {

      @Override
      public String getName() {
        return "activity";
      }

      @Override
      public void handle(Map<String, String> parameters, String value)
          throws Exception {
        Optional<TimeEntryActivity> activity = RedmineUtil.resolveActivityByName(redmineManager, value);
        activity.ifPresent(p -> HashMapDuplicates.addFormParameterEqual(parameters, "activity", String.valueOf(p.getId())));
        activity.orElseThrow(() -> new Exception(String.format(INVALID_ACTIVITY_MESSAGE, value)));
      }
    });

    for (IHandler handler : handlersList) {
      handlers.put(handler.getName(), handler);
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    HashMap<String, String> parameters = new HashMapDuplicates();

    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      handlers.get(option.getName()).handle(parameters, option.buildValue());
    }

    ResultsWrapper<TimeEntry> timeEntriesResultsWrapper = redmineManager.getTimeEntryManager().getTimeEntriesResultsWrapper(parameters);
    List<TimeEntry> timeEntries = timeEntriesResultsWrapper.getResults();
    printHeading("TIME ENTRIES");
    if (pretty) {
      for (TimeEntry timeEntry : timeEntries) {
        if (pretty) {
          println(timeEntry.toString());
        } else {

        }
      }
    } else {
      final String[][] issueTable = new String[timeEntries.size()][6];
      String[] header =
          new String[] { "ID", "Issue", "Project", "User", "Activity", "Hours", "Spent", "Comment" };

      int i = 0;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      for (TimeEntry timeEntry : timeEntries) {
        issueTable[i++] = buildRow(timeEntry, sdf);
      }

      printTable(header, issueTable);
    }
    printResults(timeEntriesResultsWrapper);
    println();
  }

  private String[] buildRow(TimeEntry timeEntry, SimpleDateFormat sdf) {
    Date spentOn = timeEntry.getSpentOn();
    return new String[] { ""+timeEntry.getId(),
        timeEntry.getIssueId().toString(),
        timeEntry.getProjectName(),
        timeEntry.getUserName(),
        timeEntry.getActivityName(),
        timeEntry.getHours().toString(),
        timeDifference ? (getTimeDifferenceAsText(spentOn) + " ago") : sdf.format(spentOn),
        timeEntry.getComment()};
  }

  
  private static class DateHandler implements IHandler {
    HashMap<String, String> filters;
    private String name;
    private String filterKey;


    {
      filters = new HashMap<>();
      filters.put("today", "t");
      filters.put("yesterday", "ld");
      filters.put("week", "w");
      filters.put("week", "lw");
      filters.put("last-week", "lw");
      filters.put("last-2-week", "l2w");
      filters.put("month", "m");
      filters.put("last-month", "lm");
      filters.put("year", "y");
    }

    private DateHandler(String name, String filterKey) {
      this.name = name;
      this.filterKey = filterKey;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void handle(Map<String, String> parameters, String value)
        throws Exception {
      //TODO: greater than, between, in last, after:
      // add new options?
      boolean validDate = false;
      if (filters.containsKey(value)) {
        validDate = true;
        HashMapDuplicates.addFormParameter(parameters, filterKey, null, filters.get(value));
      } else {
        if (filters.values().contains(value)) {
          validDate = true;
          HashMapDuplicates.addFormParameter(parameters, filterKey, null, value);
        } else {
          try {
            new SimpleDateFormat("yyyy-MM-dd").parse(value);
            validDate = true;
            HashMapDuplicates.addFormParameterEqual(parameters, filterKey, filters.get(value));
          } catch (ParseException e) {
          }
        }
      }
      if (!validDate) {
        throw new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value));
      }
    }
  }
}
