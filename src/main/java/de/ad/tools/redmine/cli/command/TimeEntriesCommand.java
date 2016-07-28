package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.TimeEntryActivity;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.RedmineUtil;

import java.io.PrintStream;
import java.util.*;

public class TimeEntriesCommand extends RedmineCommand {

    static final String INVALID_PROJECT_MESSAGE = "'%s' is not a valid project.";
    static final String INVALID_ACTIVITY_MESSAGE = "'%s' is not a valid activity.";
    static final String INVALID_PRIORITY_MESSAGE = "'%s' is not a valid priority.";
    static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";
    static final String INVALID_STATUS_MESSAGE = "'%s' is not a valid status.";
    static final String INVALID_TRACKER_MESSAGE = "'%s' is not a valid tracker.";
    static final String INVALID_SORT_MESSAGE = "'%s' is not a valid sort.";

    private static final String NAME = "timeEntries";
    private static final String DESCRIPTION = "Show time entries";
    private static final Command.Argument[] ARGUMENTS = new Command.Argument[0];
    private static final Option[] OPTIONS = new Option[] {
            new Option("project", "Only display issues for the specified project."),
            new Option("activity", "Only display issues with specified activity."),
            new Option("assignee", "Only display issues for the specified assignee.")
    };

    private static final Map<String, IHandler> handlers = new HashMap<>();

    public TimeEntriesCommand(Configuration configuration, PrintStream out, RedmineManager redmineManager) {
        super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out, redmineManager);

        /*
        &c[]=spent_on
        &c[]=issue
        &c[]=comments
        &c[]=hours
         */
        ArrayList<IHandler> handlersList = new ArrayList<>();
        handlersList.add(new IHandler() {

            @Override public String getName() {
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

            @Override public String getName() {
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

            @Override public String getName() {
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

            @Override public String getName() {
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

        HashMap<String, String>parameters = new HashMapDuplicates();

        for (Option option : getOptions()) {
            if (option.buildValue() == null) {
                continue;
            }

            handlers.get(option.getName()).handle(parameters, option.buildValue());
        }

        List<TimeEntry> timeEntries = redmineManager.getTimeEntryManager().getTimeEntries(parameters);
        printHeading("TIME ENTRIES");
        for (TimeEntry timeEntry : timeEntries) {
            println(timeEntry.toString());
        }
        println();
    }
}
