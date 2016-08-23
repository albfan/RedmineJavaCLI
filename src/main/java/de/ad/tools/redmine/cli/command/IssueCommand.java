package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.TimeEntry;
import de.ad.tools.redmine.cli.Configuration;
import org.apache.commons.lang3.StringUtils;

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
          Option.buildOptionWithoutValue("changesets", "Show changesets."),
          Option.buildOptionWithoutValue("relations", "Show relations."),
          Option.buildOptionWithoutValue("attachments", "Show attachments."),
          Option.buildOptionWithoutValue("watchers", "Show watchers."),
          Option.buildOptionWithoutValue("time", "Show time entries."),
      };

  boolean showTimeEntries;
  private static final Map<String, IHandler> handlers = new HashMap<>();

  private ArrayList<Include> includes = new ArrayList<>();

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
      public void handle() throws Exception {
        showTimeEntries = true;
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

      handlers.get(option.getName()).handle();
    }
    IssueManager issueManager = redmineManager.getIssueManager();

    Integer id = ((NumberArgument)getArguments()[0]).getValue();
    Issue issue = issueManager.getIssueById(id, includes.toArray(new Include[includes.size()]));

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
      description = description.replaceAll("<p>", "");
      description = description.replaceAll("</p>", "");
      description = description.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "");
      description = description.replaceAll("^\r\n", "");
      println(description);
    } else {
      println("(not set)");
    }
    println();
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
    Collection<Journal> journals = issue.getJournals();
    if (!journals.isEmpty()) {
      printHeading("Notes");
      for (Journal journal : journals) {
        String notes = journal.getNotes();
        if (!StringUtils.isBlank(notes)) {
          notes = notes.replaceAll("<p>", "");
          notes = notes.replaceAll("</p>", "");
          notes = notes.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "");
          println(journal.getCreatedOn().toString());
          println(notes);
          println("---");
        }
//      if (journal.getDetails() !=null) {
//        //TODO: Extract more info (change priority, status, percentage, ...
//      }
      }
    }
    println();
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
    public void handle() throws Exception {
      includes.add(include);
    }
  }

  interface IHandler {
    String getName();

    void handle() throws Exception;
  }
}
