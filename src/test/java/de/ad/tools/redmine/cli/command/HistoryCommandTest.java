package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalDetail;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HistoryCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;

  private IssueManager issueManager;

  private HistoryCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConfigured()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);

    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);

    when(redmineManager.getIssueManager()).thenReturn(issueManager);

    command = new HistoryCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[] { "1" };

    Issue issue = createMockIssue(1);
    when(issueManager.getIssueById(1, Include.journals)).thenReturn(issue);

    command.process(arguments);

    //Maybe it's faster to use byte[]
    //However, String provides a better comparison in case of differences
    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/HistoryCommandOutput.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  private Issue createMockIssue(int id) {
    Tracker tracker = mock(Tracker.class);
    when(tracker.getName()).thenReturn("Bug");

    Date createdOn = Date.from(
        LocalDateTime.now()
            .minusHours(1)
            .atZone(ZoneId.systemDefault())
            .toInstant());
    Date updatedOn = Date.from(
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

    User author = mock(User.class);
    when(author.getFirstName()).thenReturn("John");
    when(author.getLastName()).thenReturn("Doe");
    when(author.getFullName()).thenReturn("John Doe");
    String fullName = author.getFullName();

    Issue issue = mock(Issue.class);
    when(issue.getTracker()).thenReturn(tracker);
    when(issue.getId()).thenReturn(id);
    when(issue.getSubject()).thenReturn("Subject of #" + id);
    when(issue.getCreatedOn()).thenReturn(createdOn);
    when(issue.getUpdatedOn()).thenReturn(updatedOn);
    when(issue.getAuthorName()).thenReturn("John Doe");
    Integer authorId = author.getId();
    when(issue.getAuthorId()).thenReturn(authorId);
    when(issue.getAuthorName()).thenReturn(fullName);
    when(issue.getDescription()).thenReturn("Description of #" + id);

    Journal journal1 = mock(Journal.class);
    when(journal1.getUser()).thenReturn(author);
    when(journal1.getCreatedOn()).thenReturn(updatedOn);
    when(journal1.getNotes()).thenReturn("This is a note.");

    JournalDetail journalDetail1 = mock(JournalDetail.class);
    when(journalDetail1.getName()).thenReturn("Status");
    when(journalDetail1.getOldValue()).thenReturn("New");
    when(journalDetail1.getNewValue()).thenReturn("In Progress");

    when(journal1.getDetails()).thenReturn(
        Arrays.asList(journalDetail1));

    Journal journal2 = mock(Journal.class);
    when(journal2.getUser()).thenReturn(author);
    when(journal2.getCreatedOn()).thenReturn(updatedOn);

    JournalDetail journalDetail2 = mock(JournalDetail.class);
    when(journalDetail2.getName()).thenReturn("Priority");
    when(journalDetail2.getOldValue()).thenReturn("Normal");
    when(journalDetail2.getNewValue()).thenReturn("High");

    when(journal2.getDetails()).thenReturn(
        Arrays.asList(journalDetail2));

    List<Journal> journals = Arrays.asList(journal1, journal2);
    when(issue.getJournals()).thenReturn(journals);

    return issue;
  }
}
