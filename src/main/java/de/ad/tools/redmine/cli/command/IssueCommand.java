package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.*;
import de.ad.tools.redmine.cli.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.*;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class IssueCommand extends RedmineCommand {

  private static final String NAME = "issue";
  private static final String DESCRIPTION = "Display issue details.";
  private static final Argument[] ARGUMENTS = new Argument[] {
          new NumberArgument("id", "The ID of the issue to display.", false)
      };
  private static final Option[] OPTIONS = new Option[] {
          Option.buildOptionWithoutValue("journals", "Show journals."),
          new Option("ignore-journal-details", "Ignore list of journal details"),
          Option.buildOptionWithoutValue("changesets", "Show changesets."),
          Option.buildOptionWithoutValue("relations", "Show relations."),
          Option.buildOptionWithoutValue("attachments", "Show attachments."),
          Option.buildOptionWithoutValue("watchers", "Show watchers."),
          Option.buildOptionWithoutValue("time", "Show time entries."),
          Option.buildOptionWithoutValue("raw", "Show json output for issue")
      };

  boolean showTimeEntries;
  private static final Map<String, IHandler> handlers = new HashMap<>();

  private ArrayList<Include> includes = new ArrayList<>();
  private boolean raw;
  private List<String> ignoreJournalDetails = new ArrayList<>();

  public IssueCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, OPTIONS, configuration, out, redmineManager);

    ArrayList<IHandler> handlers = new ArrayList<>();
    handlers.add(new IncludeHandler("journals", Include.journals, includes));
    handlers.add(new IncludeHandler("changesets", Include.changesets, includes));
    handlers.add(new IncludeHandler("relations", Include.relations, includes));
    handlers.add(new IncludeHandler("attachments", Include.attachments, includes));
    handlers.add(new IncludeHandler("watchers", Include.watchers, includes));
    handlers.add(new IHandler() {
      @Override
      public String getName() {
        return "time";
      }

      @Override
      public void handle(String value) throws Exception {
        showTimeEntries = true;
      }
    });
    handlers.add(new IHandler() {
      @Override
      public String getName() {
        return "raw";
      }

      @Override
      public void handle(String value) throws Exception {
        raw = true;
      }
    });
    handlers.add(new IHandler() {
      @Override
      public String getName() {
        return "ignore-journal-details";
      }

      @Override
      public void handle(String value) throws Exception {
        ignoreJournalDetails =  Arrays.asList(value.toLowerCase().split("\\s*,\\s*"));
      }
    });

    for (IHandler handler : handlers) {
      IssueCommand.handlers.put(handler.getName(), handler);
    }
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    for (Option option : getOptions()) {
      if (option.buildValue() == null) {
        continue;
      }

      handlers.get(option.getName()).handle(option.buildValue());
    }
    IssueManager issueManager = redmineManager.getIssueManager();

    Integer id = ((NumberArgument)getArguments()[0]).getValue();

    if (raw) {
      String response = issueManager.getRawIssueById(id, includes.toArray(new Include[includes.size()]));
      printRawResponse(response);
    } else {
      Issue issue = issueManager.getIssueById(id, includes.toArray(new Include[includes.size()]));
      printIssue(issue);
    }
  }

  private void printRawResponse(String rawResponse) {
    try {
      JSONObject json = new JSONObject(rawResponse);
      println(json.toString(2));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void printIssue(Issue issue) throws RedmineException {
    printHeader(issue);
    printDetails(issue);
    printDescription(issue);
    if (showTimeEntries) {
      List<TimeEntry> timeEntries = redmineManager.getTimeEntryManager().getTimeEntriesForIssue(issue.getId());
      printTimeEntries(timeEntries);
    }
    printJournals(issue);
  }

  private void printHeader(Issue issue) {
    String heading =
        String.format("%s #%d", issue.getTracker().getName(), issue.getId());
    printHeading(heading);

    println(issue.getSubject());
    String createdText = getTimeDifferenceAsText(issue.getCreatedOn());
    String updatedText = "";

    if (issue.getCreatedOn().before(issue.getUpdatedOn())) {
      updatedText = getTimeDifferenceAsText(issue.getUpdatedOn());
      updatedText = String.format("Updated %s ago.", updatedText);
    }
    println("Added by %s %s ago. %s", issue.getAuthorName(),
        createdText, updatedText);
    println();
  }

  private void printDetails(Issue issue) {
    String[][] details = new String[1][3];
    String[] header = new String[] { "Status", "Done Ratio", "Priority", "Assignee" };

    String assignee = issue.getAssigneeName() != null
            ? issue.getAssigneeName()
            : "(not assigned)";
    details[0] = new String[] { issue.getStatusName(), issue.getDoneRatio().toString(), issue.getPriorityText(), assignee};

    printTable(header, details);
    println();
  }

  private void printDescription(Issue issue) {
    printHeading("Description");

    String description = issue.getDescription();
    if (description.length() > 0) {
      description = removeHtml(description);
      println(description);
    } else {
      println("(not set)");
    }
    println();
  }

  private String removeHtml(String text) {
    text = text.replaceAll("<p>", "");
    text = text.replaceAll("</p>", "");
    text = text.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "");
    text = text.replaceAll("^\r\n", "");
    return text;
  }

  private void printTimeEntries(List<TimeEntry> timeEntries) {
    printHeading("Time Entries");

    for (TimeEntry timeEntry : timeEntries) {
      String comment = timeEntry.getComment();
      if (!StringUtils.isBlank(comment)) {
        comment = ": "+comment;
      }
      println("User \"" + timeEntry.getUserName() + "\" spent " + timeEntry.getHours()
              + " hours doing " + timeEntry.getActivityName()+ comment);
    }
    println();
  }

  private void printJournals(Issue issue) {
    ArrayList<Journal> journals = new ArrayList<>(issue.getJournals());
    Collections.sort(journals, (o1, o2) -> o1.getCreatedOn().compareTo(o2.getCreatedOn()));
    if (!journals.isEmpty()) {
      printHeading("Notes");
      for (Journal journal : journals) {
        String notes = journal.getNotes();
        println(journal.getCreatedOn().toString());
        if (!StringUtils.isBlank(notes)) {
          notes = removeHtml(notes);
          println(notes);
        }
        if (journal.getDetails() != null) {
          for (JournalDetail journalDetail : journal.getDetails()) {
            String property = journalDetail.getProperty();
            String name = journalDetail.getName();
            if (ignoreJournalDetails.contains(name)
                    || name.endsWith("_id") && ignoreJournalDetails.contains(name.replaceAll("_id\\$", ""))) {
              continue;
            }
            String oldValue = journalDetail.getOldValue();
            String newValue = journalDetail.getNewValue();
            if (name.equals("description")) {
              name = "Description";
              oldValue = "\n"+removeHtml(oldValue)+"\n";
              newValue = "\n"+removeHtml(newValue);
            } else if (name.equals("assigned_to_id")) {
              name = "Assigned";
              if (oldValue != null) {
                try {
                  User user = redmineManager.getUserManager().getUserById(Integer.parseInt(oldValue));
                  oldValue = user.getFullName();
                } catch (RedmineException e) {
                  e.printStackTrace();
                }
              }
              if (newValue != null) {
                try {
                  User user = redmineManager.getUserManager().getUserById(Integer.parseInt(newValue));
                  newValue = user.getFullName();
                } catch (RedmineException e) {
                  e.printStackTrace();
                }
              }
            } else if (name.equals("priority_id")) {
              name = "Priority";
              try {
                final int oldId = Integer.parseInt(oldValue);
                Optional<IssuePriority> priority = redmineManager.getIssueManager().getIssuePriorities().stream().filter(p -> p.getId().equals(oldId)).findFirst();
                if (priority.isPresent()) {
                  oldValue = priority.get().getName();
                }
              } catch (RedmineException e) {
                e.printStackTrace();
              }
              try {
                final int newId = Integer.parseInt(newValue);
                Optional<IssuePriority> priority = redmineManager.getIssueManager().getIssuePriorities().stream().filter(p -> p.getId().equals(newId)).findFirst();
                if (priority.isPresent()) {
                  newValue = priority.get().getName();
                }
              } catch (RedmineException e) {
                e.printStackTrace();
              }
            } else if (name.equals("status_id")) {
              name = "Status";
              try {
                final int oldId = Integer.parseInt(oldValue);
                Optional<IssueStatus> priority = redmineManager.getIssueManager().getStatuses().stream().filter(p -> p.getId().equals(oldId)).findFirst();
                if (priority.isPresent()) {
                  oldValue = priority.get().getName();
                }
              } catch (RedmineException e) {
                e.printStackTrace();
              }
              try {
                final int newId = Integer.parseInt(newValue);
                Optional<IssueStatus> priority = redmineManager.getIssueManager().getStatuses().stream().filter(p -> p.getId().equals(newId)).findFirst();
                if (priority.isPresent()) {
                  newValue = priority.get().getName();
                }
              } catch (RedmineException e) {
                e.printStackTrace();
              }
            }

            if(newValue != null) {
              String setTo = oldValue == null ? " set to " : ": " + oldValue + " -> ";
              println(name + setTo + newValue);
            } else {
              if (oldValue != null) {
                println(name + " (removed): " + oldValue);
              }
            }
          }
        }
        println("---");
      }
    }
  }

  private static class IncludeHandler implements IHandler {
    String name;
    Include include;
    ArrayList<Include> includes;

    public IncludeHandler(String name, Include include, ArrayList<Include> includes) {
      this.name = name;
      this.include = include;
      this.includes = includes;
    }

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Include getInclude() {
      return include;
    }

    public void setInclude(Include include) {
      this.include = include;
    }

    @Override
    public void handle(String value) throws Exception {
      includes.add(include);
    }
  }

  interface IHandler {
    String getName();

    void handle(String value) throws Exception;
  }
}
