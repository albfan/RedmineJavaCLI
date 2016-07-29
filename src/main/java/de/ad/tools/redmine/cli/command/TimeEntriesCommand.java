package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.TimeEntryActivity;
import de.ad.tools.redmine.cli.Configuration;
import de.ad.tools.redmine.cli.util.HashMapDuplicates;
import de.ad.tools.redmine.cli.util.RedmineUtil;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeEntriesCommand extends RedmineCommand {

    static final String INVALID_PROJECT_MESSAGE = "'%s' is not a valid project.";
    static final String INVALID_ACTIVITY_MESSAGE = "'%s' is not a valid activity.";
    static final String INVALID_ASSIGNEE_MESSAGE = "'%s' is not a valid assignee.";

    private static final String NAME = "time-entries";
    private static final String DESCRIPTION = "Show time entries";
    private static final Command.Argument[] ARGUMENTS = new Command.Argument[0];
    private static final Option[] OPTIONS = new Option[] {
            new Option("project", "Only display issues for the specified project."),
            new Option("activity", "Only display issues with specified activity."),
            new Option("assignee", "Only display issues for the specified assignee."),
            new Option("created-on", "Only display issues created on range marked."),
            new Option("updated-on", "Only display issues updated on range marked."),
            new Option("start-date", "Only display issues started on range marked."),
            new Option("end-date", "Only display issues ended on range marked."),
            new Option("expiration-date", "Only display issues expired on range marked.")
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
        handlersList.add(new DateHandler("created-on", "created_on"));
        handlersList.add(new DateHandler("update-on", "update_on"));
        handlersList.add(new DateHandler("start-date", "start_date"));
        handlersList.add(new DateHandler("end-date", "end_date"));
        handlersList.add(new DateHandler("expiration-date", "expiration_date"));

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

        @Override public String getName() {
            return name;
        }

        @Override
        public void handle(Map<String, String> parameters, String value)
                throws Exception {
            if (filters.containsKey(value)) {
                HashMapDuplicates.addFormParameter(parameters, filterKey, null, filters.get(value));
            } else {
                //TODO: greater than, between, in last, after:
                // add new options?
                boolean validDate;
                try {
                    new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    validDate = true;
                } catch (ParseException e) {
                    validDate = false;
                }
                if (validDate) {
                    HashMapDuplicates.addFormParameterEqual(parameters, filterKey, filters.get(value));
                } else {
                    throw new Exception(String.format(INVALID_ASSIGNEE_MESSAGE, value));
                }
            }
        }
    }
}
