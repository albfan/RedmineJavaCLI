package de.ad.tools.redmine.cli.command;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Tracker;
import de.ad.tools.redmine.cli.Configuration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static de.ad.tools.redmine.cli.test.TestHelper.resourceToByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListCommandTest {
  private Configuration configuration;
  private PrintStream out;
  private ByteArrayOutputStream stream;
  private RedmineManager redmineManager;
  private IssueManager issueManager;

  private ListCommand command;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    configuration = mock(Configuration.class);
    when(configuration.isConnected()).thenReturn(true);

    stream = new ByteArrayOutputStream();
    out = new PrintStream(stream);

    redmineManager = mock(RedmineManager.class);
    issueManager = mock(IssueManager.class);

    command = new ListCommand(configuration, out, redmineManager);
  }

  @Test
  public void testListStatus() throws Exception {
    String[] arguments = new String[] { "status" };

    List<IssueStatus> statuses = createDummyStatuses();

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(issueManager.getStatuses()).thenReturn(statuses);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/ListCommandOutput1.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testListTracker() throws Exception {
    String[] arguments = new String[] { "tracker" };

    List<Tracker> trackers = createDummyTrackers();

    when(redmineManager.getIssueManager()).thenReturn(issueManager);
    when(issueManager.getTrackers()).thenReturn(trackers);

    command.process(arguments);

    String actual = new String(stream.toByteArray());
    String expected =
        new String(resourceToByteArray("/ListCommandOutput2.txt"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testListInvalidEntity() throws Exception {
    String[] arguments = new String[] { "invalid" };

    exception.expect(Exception.class);
    exception.expectMessage(
        String.format(ListCommand.INVALID_ENTITY_MESSAGE, "invalid"));
    command.process(arguments);
  }

  private List<IssueStatus> createDummyStatuses() {
    IssueStatus newStatus = mock(IssueStatus.class);
    when(newStatus.getName()).thenReturn("New");
    when(newStatus.getId()).thenReturn(1);
    when(newStatus.isDefaultStatus()).thenReturn(true);
    when(newStatus.isClosed()).thenReturn(false);

    IssueStatus closedStatus = mock(IssueStatus.class);
    when(closedStatus.getName()).thenReturn("Closed");
    when(closedStatus.getId()).thenReturn(2);
    when(closedStatus.isDefaultStatus()).thenReturn(false);
    when(closedStatus.isClosed()).thenReturn(true);

    return Arrays.asList(newStatus, closedStatus);
  }

  private List<Tracker> createDummyTrackers() {
    Tracker bugTracker = mock(Tracker.class);
    when(bugTracker.getName()).thenReturn("Bug");
    when(bugTracker.getId()).thenReturn(1);

    Tracker featureTracker = mock(Tracker.class);
    when(featureTracker.getName()).thenReturn("Feature");
    when(featureTracker.getId()).thenReturn(2);

    return Arrays.asList(bugTracker, featureTracker);
  }
}
