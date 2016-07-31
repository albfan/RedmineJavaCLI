package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.*;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.RedmineUtil;
import org.omg.CORBA.INVALID_ACTIVITY;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CreateTimeEntryCommand extends RedmineCommand {
  static final String SUCCESS_MESSAGE = "Time entry #%d succesfully created.";

  static final String INVALID_ACTIVITY_MESSAGE = "'%s' is not a valid activity.";
  static final String INVALID_USER_MESSAGE = "'%s' is not a valid user.";
  static final String INVALID_HOURS_MESSAGE = "'%s' is not a valid hour entry.";

  private static final String NAME = "create-time-entry";
  private static final String DESCRIPTION = "Create a new time entry.";
  private static final Argument[] ARGUMENTS =
      new Argument[] { 
          new NumberArgument("issueId", "The ID of the issue to add this time entry to.", false),
          new TextArgument("hours", "The number of hours spent.", false),
          new TextArgument("comment", "The comment for this time entry.", true)
      };
  private static final Option[] OPTIONS = new Option[] {
      Option.buildOptionWithDefaultValue("activity", "The activity of the time entry to create.", "default"),
      Option.buildOptionWithDefaultValue("user", "The name of user to add time entry to.", "me")
  };

  private static final Map<String, IHandler> handlers = new HashMap<>();

  public CreateTimeEntryCommand(Configuration configuration, PrintStream out,
                                RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out,
        redmineManager);

    ArrayList<IHandler> handlers = new ArrayList<>();
    handlers.add(new IHandler() {
      @Override public String getName() {
        return "activity";
      }

      @Override
      public void handle(TimeEntry timeentry, String value)
          throws Exception {
        Optional<TimeEntryActivity> activity;
        if (value.equals("default")) {
          activity = redmineManager.getTimeEntryManager().getTimeEntryActivities().stream().filter(TimeEntryActivity::isDefault).findFirst();
        } else {
          activity = RedmineUtil.resolveActivityByName(redmineManager, value);
        }
        activity.ifPresent(a -> timeentry.setActivityId(a.getId()));
        activity.orElseThrow(() -> new Exception(String.format(INVALID_ACTIVITY_MESSAGE, value)));
      }
    });
    handlers.add(new IHandler() {
      @Override public String getName() {
        return "user";
      }

      @Override
      public void handle(TimeEntry timeentry, String value)
          throws Exception {
        try {
          int userId = Integer.parseInt(value);
          User user = redmineManager.getUserManager().getUserById(userId);
          timeentry.setUserId(user.getId());
        } catch (NumberFormatException e) {
          try {
            Optional<User> user = RedmineUtil.resolveUserByName(redmineManager, value);
            user.ifPresent(u -> timeentry.setUserId(u.getId()));
            user.orElseThrow(() -> new Exception(String.format(INVALID_USER_MESSAGE, value)));
          } catch (NotAuthorizedException ex) {
            throw new Exception("You are not authorized to get user by name");
          }
        }
      }
    });
    for (IHandler handler : handlers) {
      CreateTimeEntryCommand.handlers.put(handler.getName(), handler);
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);
    Integer issueId = ((NumberArgument)getArguments()[0]).getValue();
    float hours = processHours();
    String comment = ((TextArgument)getArguments()[2]).getValue();

    TimeEntryManager timeEntryManager = redmineManager.getTimeEntryManager();

    TimeEntry timeEntry = TimeEntryFactory.create();
    timeEntry.setIssueId(issueId);
    timeEntry.setComment(comment);
    timeEntry.setHours(hours);

    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      handlers.get(option.getName())
          .handle(timeEntry, option.buildValue());
    }

    TimeEntry newTimeEntry = timeEntryManager.createTimeEntry(timeEntry);

    println(SUCCESS_MESSAGE, newTimeEntry.getId());
  }

  public float processHours() throws Exception {
    String hours = ((TextArgument)getArguments()[1]).getValue();
    Float floatHours = null;
    try {
      if (hours.contains("h")) {
        String[] split = hours.split("h");
        if (split.length >= 1) {
          floatHours = Float.parseFloat(split[0]);
          if (split.length == 2) {
            String min = split[1];
            if (min.endsWith("m")) {
              min = min.substring(0, min.length() - 2);
            }
            float fMin = Integer.parseInt(min) / 60f;
            floatHours += fMin;
          }
        }
      } else {
        if (hours.endsWith("m")) {
          String min = hours.substring(0, hours.length() - 2);
          int intHours = Integer.parseInt(min);
          floatHours = (intHours / 60f);
        } else {
          floatHours = Float.parseFloat(hours);
        }
      }
      if(floatHours == null) {
        throw new Exception(String.format(INVALID_HOURS_MESSAGE, hours));
      }
    } catch(NumberFormatException e) {
      throw new Exception(String.format(INVALID_HOURS_MESSAGE, hours));
    }
    return floatHours;
  }

  private interface IHandler {
    String getName();

    void handle(TimeEntry timeEntry, String value) throws Exception;
  }
}
