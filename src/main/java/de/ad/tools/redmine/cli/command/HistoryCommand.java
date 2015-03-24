package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import de.ad.tools.redmine.cli.Configuration;

import java.io.PrintStream;

import static de.ad.tools.redmine.cli.util.DateUtil.getTimeDifferenceAsText;

public class HistoryCommand extends RedmineCommand {

  private static final String NAME = "history";
  private static final String DESCRIPTION = "Display issue history.";
  private static final Argument[] ARGUMENTS =
      new Argument[] {
          new NumberArgument("id", "The ID of the issue to display.", false) };

  public HistoryCommand(Configuration configuration, PrintStream out,
      RedmineManager redmineManager) {
    super(NAME, DESCRIPTION, "", ARGUMENTS, configuration, out,
        redmineManager);
  }

  @Override
  public void process(String[] arguments) throws Exception {
    super.process(arguments);

    IssueManager issueManager = redmineManager.getIssueManager();

    Integer id = ((NumberArgument)getArguments()[0]).getValue();
    Issue issue = issueManager.getIssueById(id, Include.journals);

    printHeader(issue);
    printJournals(issue);
  }

  private void printHeader(Issue issue) {
    println("%s #%d", issue.getTracker().getName(), issue.getId());
    println(issue.getSubject());
    String createdText = getTimeDifferenceAsText(issue.getCreatedOn());
    String updatedText = "";

    if (issue.getCreatedOn().before(issue.getUpdatedOn())) {
      updatedText = getTimeDifferenceAsText(issue.getUpdatedOn());
      updatedText = String.format("Updated %s ago.", updatedText);
    }
    println("Added by %s %s ago. %s", issue.getAuthor().getFullName(),
        createdText, updatedText);
    println();
  }

  private void printJournals(Issue issue) {
    printHeading("History");

    issue.getJournals().forEach(this::printJournal);
  }

  private void printJournal(Journal journal) {
    println("---");
    println("Updated by %s %s ago.", journal.getUser().getFullName(),
        getTimeDifferenceAsText(journal.getCreatedOn()));

    journal.getDetails().forEach(this::printJournalDetails);

    if (journal.getNotes() != null && journal.getNotes().length() > 0) {
      println(journal.getNotes());
    }
  }

  private void printJournalDetails(JournalDetail journalDetail) {
    println(" - %s changed from %s to %s", journalDetail.getName(),
        journalDetail.getOldValue(), journalDetail.getNewValue());
  }
}
