package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.bean.User;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IssueCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;

  private IssueManager issueManager;

  private IssueCommand command;

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

    command = new IssueCommand(configuration, out, redmineManager);
  }

  @Test
  public void testCommand() throws Exception {
    String[] arguments = new String[] { "1" };

    Issue issue = createMockIssue(1);
    when(issueManager.getIssueById(1)).thenReturn(issue);

    command.process(arguments);

    //Maybe it's faster to use byte[]
    //However, String provides a better comparison in case of differences
    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/IssueCommandOutput1.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testCommandWithAssigneeAndDescriptionNotSet() throws Exception {
    String[] arguments = new String[] { "1" };

    Issue issue = createAnotherMockIssue(1);
    when(issueManager.getIssueById(1)).thenReturn(issue);

    command.process(arguments);

    //Maybe it's faster to use byte[]
    //However, String provides a better comparison in case of differences
    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/IssueCommandOutput2.txt"));

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
    when(author.getFullName()).thenReturn("John Doe");

    ArrayList<Journal> journals = new ArrayList<>();
    Journal journal = Mockito.mock(Journal.class);
    when(journal.getNotes()).thenReturn("a comment");
    Date date = Mockito.mock(Date.class);
    when(date.toString()).thenReturn("Sun Jul 31 00:00:00 UTC 2016");
    when(journal.getCreatedOn()).thenReturn(date);
    journals.add(journal);

    Issue issue = mock(Issue.class);
    when(issue.getTracker()).thenReturn(tracker);
    when(issue.getId()).thenReturn(id);
    when(issue.getSubject()).thenReturn("Subject of #" + id);
    when(issue.getCreatedOn()).thenReturn(createdOn);
    when(issue.getUpdatedOn()).thenReturn(updatedOn);
    Integer authorId = author.getId();
    when(issue.getAuthorId()).thenReturn(authorId);
    String fullName = author.getFullName();
    when(issue.getAuthorName()).thenReturn(fullName);
    when(issue.getAssigneeName()).thenReturn("John");
    when(issue.getAssigneeId()).thenReturn(1);
    when(issue.getDescription()).thenReturn("Description of #" + id);
    when(issue.getJournals()).thenReturn(journals);


    return issue;
  }

  private Issue createAnotherMockIssue(int id) {
    Tracker tracker = mock(Tracker.class);
    when(tracker.getName()).thenReturn("Bug");

    Date createdOn = new Date();
    Date updatedOn = new Date();

    User author = mock(User.class);
    when(author.getFullName()).thenReturn("John Doe");

    ArrayList<Journal> journals = new ArrayList<>();
    Journal journal = Mockito.mock(Journal.class);
    when(journal.getNotes()).thenReturn("a comment");
    Date date = Mockito.mock(Date.class);
    when(date.toString()).thenReturn("Sun Jul 31 00:00:00 UTC 2016");
    when(journal.getCreatedOn()).thenReturn(date);
    journals.add(journal);

    Issue issue = mock(Issue.class);
    when(issue.getTracker()).thenReturn(tracker);
    when(issue.getId()).thenReturn(id);
    when(issue.getSubject()).thenReturn("Subject of #" + id);
    when(issue.getCreatedOn()).thenReturn(createdOn);
    when(issue.getUpdatedOn()).thenReturn(updatedOn);
    Integer authorId = author.getId();
    when(issue.getAuthorId()).thenReturn(authorId);
    String fullName = author.getFullName();
    when(issue.getAuthorName()).thenReturn(fullName);
    when(issue.getAssigneeName()).thenReturn("John");
    when(issue.getAssigneeId()).thenReturn(1);
    when(issue.getDescription()).thenReturn("");
    when(issue.getJournals()).thenReturn(journals);

    return issue;
  }
}
